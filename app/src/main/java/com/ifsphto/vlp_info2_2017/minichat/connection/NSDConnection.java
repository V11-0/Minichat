package com.ifsphto.vlp_info2_2017.minichat.connection;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.ifsphto.vlp_info2_2017.minichat.utils.Tags;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by vinibrenobr11 on 02/11/2017 at 22:23:40
 */
public class NSDConnection {

    private NsdManager mNsdManager;
    private NsdManager.DiscoveryListener mDiscoveryListener;
    private NsdManager.RegistrationListener mRegistrationListener;
    private NsdManager.ResolveListener mResolver;
    private int mLocalPort;
    private Context context;
    private NsdServiceInfo si;
    private ArrayAdapter<NsdServiceInfo> names;
    private NsdServiceInfo resolvedNsd = null;

    public final String synchronize = "a";
    public final String synchronize1 = "b";

    public NSDConnection(Context context) {
        this.context = context;
        initializeServerSocket();
        initializeRegistrationListener();
        initializeDiscoveryListener();
        initializeResolveListener();
        names = new ArrayAdapter<>(this.context, android.R.layout.simple_list_item_1);
    }

    public NsdServiceInfo resolve(NsdServiceInfo dev) {
        mNsdManager.resolveService(dev, mResolver);
        synchronized (synchronize1) {
            try {
                synchronize1.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return resolvedNsd;
    }

    public void discover() {
        mNsdManager.discoverServices(Tags.Nsd.TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    public void register(String name) {
        registerService(name);
    }

    public ArrayAdapter<NsdServiceInfo> getDevices() {
        return names;
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
                resolvedNsd = nsdServiceInfo;
                Log.i("Service", "Resolvido");
                synchronized (synchronize1) {
                    synchronize1.notify();
                }
            }
        };
    }

    private void registerService(String name) {

        si = new NsdServiceInfo();
        si.setServiceName(name);
        si.setServiceType(Tags.Nsd.TYPE);
        si.setPort(mLocalPort);

        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);

        mNsdManager.registerService(si, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
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
                // A service was found!  Do something with it.
                if (service.getServiceType().equals(Tags.Nsd.TYPE)) {
                    if (service.getServiceName().equals(si.getServiceName()))
                        Log.i(Tags.LOG_TAG, "Voce se achou");
                    else {
                        names.add(service); // FIXME: 07/11/2017 Erros
                        Log.i(Tags.LOG_TAG, "Dispositivo Encontrado");
                        synchronized (synchronize) {
                            synchronize.notify();
                        }
                    }
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e(Tags.LOG_TAG, "service lost" + service);
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
}