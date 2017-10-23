package com.ifsphto.vlp_info2_2017.minichat.security;

import android.util.Base64;

/**
 * Created by vinibrenobr11 on 18/10/2017 at 22:35:07<br></br>
 *
 * Classe que criptografa strings
 */
public abstract class Encrypt {

    /**
     * Criptografa com algorito Base64 uma string
     * @param s String a ser criptografada
     * @return String criptografada
     */
    public static String encryptPass(String s) {
        return Base64.encodeToString(s.getBytes(), Base64.DEFAULT);
    }
}
