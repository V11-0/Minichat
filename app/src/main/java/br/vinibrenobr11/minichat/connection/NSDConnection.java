package br.vinibrenobr11.minichat.connection;

import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.ArrayAdapter;

import br.vinibrenobr11.minichat.database.DbManager;
import br.vinibrenobr11.minichat.page.MainPage;
import br.vinibrenobr11.minichat.services.MessageService;
import br.vinibrenobr11.minichat.utils.Tags;

import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.Observable;

/**
 * Created by vinibrenobr11 on 02/11/2017 at 22:23:40
 */
public class NSDConnection extends Observable {

    private NsdManager mNsdManager;
    private NsdManager.DiscoveryListener mDiscoveryListener;
    private NsdManager.RegistrationListener mRegistrationListener;
    private NsdManager.ResolveListener mResolver;
    private MainPage activity;
    private ArrayAdapter<NsdServiceInfo> devices;

    public NsdServiceInfo si;

    public NSDConnection(MainPage activity) {
        this.activity = activity;
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
        try {
            registerService(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayAdapter<NsdServiceInfo> getDevices() {
        return devices;
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

    private void registerService(String name) throws IOException {

        ServerSocket mServer = new ServerSocket(0);
        int port = mServer.getLocalPort();

        si = new NsdServiceInfo();
        si.setServiceName(name);
        si.setServiceType(Tags.Nsd.TYPE);
        si.setPort(port);

        mServer.close();

        mNsdManager = (NsdManager) activity.getSystemService(Context.NSD_SERVICE);

        if (mNsdManager != null) {
            mNsdManager.registerService(si, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
            this.startNewMessagesListener(port);
        } else
            throw new NullPointerException("NsdManager is null");
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
                } else {

                    WifiManager wifiManager = (WifiManager) activity.getApplicationContext()
                            .getSystemService(Context.WIFI_SERVICE);

                    int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

                    ipAddress = (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) ?
                            Integer.reverseBytes(ipAddress) : ipAddress;

                    byte[] ipBytes = BigInteger.valueOf(ipAddress).toByteArray();

                    String ip = "Err";

                    try {
                        ip = InetAddress.getByAddress(ipBytes).getHostAddress();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }

                    activity.setDrawerText(nsdServiceInfo.getServiceName(), ip + ":" +
                            si.getPort());
                }
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

    private void startNewMessagesListener(int port) {
        Intent service = new Intent(activity, MessageService.class);
        service.putExtra("ThisHost", si);
        service.putExtra("Port", port);

        activity.startService(service);
    }

    public static void sendMessage(Context context, NsdServiceInfo me
            , NsdServiceInfo dest, String msg) throws Exception {

        Socket socket = new Socket(dest.getHost(), dest.getPort());
        DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());

        String name = me.getServiceName();

        dOut.writeUTF(name);
        dOut.writeUTF(msg);
        dOut.writeInt(me.getPort());
        dOut.flush();

        DbManager manager = new DbManager(context, dest.getServiceName());

        if (manager.insert(name, msg)) {
            Log.i("Yeah", "Sem problemas");
        } else
            Log.e("Err", "Deu Ruim");

        dOut.close();
        manager.close();
        socket.close();
    }
}