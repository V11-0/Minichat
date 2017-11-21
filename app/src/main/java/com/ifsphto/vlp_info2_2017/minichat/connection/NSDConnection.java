package com.ifsphto.vlp_info2_2017.minichat.connection;

import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.ifsphto.vlp_info2_2017.minichat.database.DbManager;
import com.ifsphto.vlp_info2_2017.minichat.page.MainPage;
import com.ifsphto.vlp_info2_2017.minichat.services.MessageService;
import com.ifsphto.vlp_info2_2017.minichat.utils.Tags;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;

/**
 * Created by vinibrenobr11 on 02/11/2017 at 22:23:40
 */
public class NSDConnection extends Observable {

    private NsdManager mNsdManager;
    private NsdManager.DiscoveryListener mDiscoveryListener;
    private NsdManager.RegistrationListener mRegistrationListener;
    private NsdManager.ResolveListener mResolver;
    private int mLocalPort;
    private MainPage activity;
    private NsdServiceInfo si;
    private ArrayAdapter<NsdServiceInfo> devices;

    public NSDConnection(MainPage activity) {
        this.activity = activity;
        initializeServerSocket();
        initializeRegistrationListener();
        initializeDiscoveryListener();
        initializeResolveListener();
        devices = new ArrayAdapter<>(this.activity, android.R.layout.simple_list_item_1);
        this.addObserver(activity);
    }

    public void resolve(NsdServiceInfo dev) {
        mNsdManager.resolveService(dev, mResolver);
    }

    public void discover() {
        mNsdManager.discoverServices(Tags.Nsd.TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    public void register(String name) {
        registerService(name);
    }

    public ArrayAdapter<NsdServiceInfo> getDevices() {
        return devices;
    }

    private void initializeServerSocket() {
        ServerSocket mServerSocket = null;
        try {
            mServerSocket = new ServerSocket(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mLocalPort = mServerSocket.getLocalPort();
    }

    private void initializeResolveListener() {
        mResolver = new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo nsdServiceInfo, int i) {
                throw new UnsupportedOperationException("Error " + i);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo nsdServiceInfo) {
                Log.i("Service", "Resolvido");
                sendResolved(nsdServiceInfo);
            }
        };
    }

    private void registerService(String name) {

        si = new NsdServiceInfo();
        si.setServiceName(name);
        si.setServiceType(Tags.Nsd.TYPE);
        si.setPort(mLocalPort);

        mNsdManager = (NsdManager) activity.getSystemService(Context.NSD_SERVICE);

        mNsdManager.registerService(si, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
        this.startNewMessagesListener();
    }

    private void notifyMainPage() {
        setChanged();
        notifyObservers();
    }

    private void sendResolved(NsdServiceInfo resolved) {
        setChanged();
        notifyObservers(resolved);
    }

    private void initializeRegistrationListener() {

        mRegistrationListener = new NsdManager.RegistrationListener() {
            @Override
            public void onRegistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
                Log.e(Tags.LOG_TAG, "Falha ao registar");
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
                Log.e(Tags.LOG_TAG, "Falha ao unRegistrar serviço");
            }

            @Override
            public void onServiceRegistered(NsdServiceInfo nsdServiceInfo) {
                if (!si.getServiceName().equals(nsdServiceInfo.getServiceName())) {
                    Log.i("Error", "Deu ruim no nome");
                    finishEverything();
                    activity.nameHasCollided();
                } else
                    activity.setDrawerText(nsdServiceInfo.getServiceName(), si.getPort());
                Log.i(Tags.LOG_TAG, "Sucesso ao registrar serviço");
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo nsdServiceInfo) {
                Log.i(Tags.LOG_TAG, "Sucesso ao Desregistrar serviço");
            }
        };
    }

    private void initializeDiscoveryListener() {

        // Instantiate a new DiscoveryListener
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            //  Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(Tags.LOG_TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                if (service.getServiceType().equals(Tags.Nsd.TYPE)) {
                    if (service.getServiceName().equals(si.getServiceName()))
                        Log.i(Tags.LOG_TAG, "Voce se achou");
                    else {
                        devices.add(service);
                        Log.i(Tags.LOG_TAG, "Dispositivo Encontrado");
                        notifyMainPage();
                    }
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e(Tags.LOG_TAG, "service lost" + service);
                activity.runOnUiThread(() -> devices.remove(service));
                notifyMainPage();
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(Tags.LOG_TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(Tags.LOG_TAG, "Discovery failed: Error code: " + errorCode);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(Tags.LOG_TAG, "Discovery failed: Error code: " + errorCode);
            }
        };
    }

    public void finishEverything() {

        try {
            mNsdManager.stopServiceDiscovery(mDiscoveryListener);
        } catch (Exception e) {
            Log.i(Tags.LOG_TAG, "Discovery Listener já estava parado");
        }

        try {
            mNsdManager.unregisterService(mRegistrationListener);
        } catch (Exception e) {
            Log.i(Tags.LOG_TAG, "RegistrationListener já Estava parado");
        }
    }

    private void startNewMessagesListener() {
        Intent service = new Intent(activity, MessageService.class);
        service.putExtra("Host", si);

        activity.startService(service);
    }

    public static void sendMessage(Context context, String you, NsdServiceInfo dest, String msg)
            throws Exception {

        String table = dest.getServiceName().replace(" ", "");

        Socket socket = new Socket(dest.getHost(), dest.getPort()+1);
        DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());

        dOut.writeUTF(you);
        dOut.writeUTF(msg);
        dOut.flush();

        DbManager manager = new DbManager(context, table);

        if (manager.insert(you, msg)) {
            Log.i("Yeah", "Sem problemas");
        } else
            Log.e("Err", "Deu Ruim");

        dOut.close();
    }
}