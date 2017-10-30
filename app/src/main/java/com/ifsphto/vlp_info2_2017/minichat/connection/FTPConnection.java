package com.ifsphto.vlp_info2_2017.minichat.connection;

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

/**
 * Created by vinibrenobr11 on 21/10/2017 at 15:57:57 <br><br>
 *
 * Essa Classe disponibiliza conexões, e gerenciamento com servidores FTP
 */
public abstract class FTPConnection {

    /**
     * Obtém conexão com servidor FTP
     * @return Conexão
     * @throws IOException se ocorrer algum erro
     */
    public static FTPClient getConnection() throws IOException {

        FTPClient ftp = new FTPClient();
        ftp.setConnectTimeout(15000);
        ftp.setDataTimeout(19000);

        ftp.connect("192.168.0.254");
        ftp.login("usuario", "teste");

        ftp.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
        ftp.enterLocalPassiveMode();

        return ftp;
    }

    /**
     * Fecha a conexão com segurança
     * @param ftp conexão a ser fechada
     */
    public static void closeConnection(FTPClient ftp) {

        try {

            if (ftp.isConnected()) {
                ftp.logout();
                ftp.disconnect();
            }

        } catch (IOException ignored) {
        }
    }
}