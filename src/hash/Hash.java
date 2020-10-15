/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hash;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

/**
 *
 * @author Rafael
 */
public class Hash {
    
    // número de iterações
    private static final int ITERACOES = 0;
    
    // codificador base64
    private static final Base64.Encoder encoder = Base64.getEncoder();
    
    private static long teste__;
    public static void teste__() {
        teste__ = 0;
        int rodadas = 10;
        System.out.println("Testando...");
        for (int i = 0; i < rodadas; i++) {
            Hash.converter("1234567812345678123456781234567812345678123456781234567812345678");
        }
        System.out.println((double)teste__ / rodadas);
    }

    public static String converter(String senha) {
        
        // gerar o sal
        byte[] sal_bytes = new byte[9];
        new Random().nextBytes(sal_bytes);
        String sal = encoder.encodeToString(sal_bytes);
        
        // retornar sal + $ + hash. em base64
        return sal + '$' + hash_string(senha + sal);
    }

    public static boolean verificar(String senha, String convertida) {
        
        // dividir em [sal, senha]
        String[] split = convertida.split("\\$");
        
        // calcular o hash + sal
        String hash = hash_string(senha + split[0]);
        
        // comparar a igualdade do hash
        return split[1].equals(hash);
    }
    
    // retornar o hash da senha, em base64
    private static String hash_string(String senha) {
        long start = System.nanoTime();
        
        // converter a senha em blocos com padding para 128 bits
        int[] padding = padding(senha);

        // calcular o primeiro hash
        int[] hash = hash(padding);
        
        int i = 0;
        for (; System.nanoTime() - start < 500000000; i++) {
            hash = hash(hash);
        }

        // calcular o hash novamente para cada iteração
//        for (int i = 0; i < ITERACOES; i++) {
//            hash = hash(hash);
//        }
        
        teste__ += i;
        
        // converter o hash em buffer de bytes
        ByteBuffer buffer = ByteBuffer.allocate(hash.length * 4);
        buffer.asIntBuffer().put(hash);
        
        // retornar o hash em base64
        return encoder.encodeToString(buffer.array());
    }
    
    // converter a senha em blocos de 128 bits (16 bytes ou 4 ints)
    private static int[] padding(String senha) {
        byte[] bytes = senha.getBytes();
        
        // completar bytes a múltiplo de 4 para empacotar em ints
        bytes = Arrays.copyOf(bytes, completar_multiplo(bytes.length, 4));
        
        // allocar buffer de ints multiplo de 128 bits
        IntBuffer padding = IntBuffer.allocate(4 + completar_multiplo(bytes.length, 16) / 4);
        
        // escrever o comprimento da senha no primeiro bloco
        padding.put(senha.length());
        
        // mover para o próximo bloco
        padding.position(4);
        
        // escrever os bytes da senha
        padding.put(ByteBuffer.wrap(bytes).asIntBuffer());

        // converter o buffer em array
        return padding.array();
    }

    // retornar o hash 512 bits da senha com padding em blocos de 128 bits
    private static int[] hash(int[] padding) {
        
        // hash anterior
        int[] h0 = iv();
        
        // hash atual
        int[] h = iv();
        
        // chave
        int[] k = new int[4];
        
        for (int i = 0; i < padding.length; i += 4) {
            // copiar o bloco atual da senha para k
             System.arraycopy(padding, i, k, 0, 4);
             
            // enctriptar h com a chave
            xxtea(h, k);
            
            for (int j = 0; j < h.length; j++) {
                // XOR de h e h0
                h[j] ^= h0[j];
                
                // copia h em h0
                h0[j] = h[j];
            }
        }
        
        return h;
    }

    // encriptar v com a chave k de 128 bits usando o algoritmo XXTEA
    private static void xxtea(int[] v, int[] k) {
        int n = v.length;
        
        int y, z, sum, p, rounds, e;
        rounds = 6 + 52 / n;
        sum = 0;
        z = v[n - 1];
        do {
            sum += 0x9e3779b9;
            e = (sum >>> 2) & 3;
            for (p = 0; p < n - 1; p++) {
                y = v[p + 1];
                z = v[p] += (((z >>> 5) ^ (y << 2)) + ((y >>> 3) ^ (z << 4))) ^ ((sum ^ y) + (k[(p & 3) ^ e] ^ z));
            }
            y = v[0];
            z = v[n - 1] += (((z >>> 5) ^ (y << 2)) + ((y >>> 3) ^ (z << 4))) ^ ((sum ^ y) + (k[(p & 3) ^ e] ^ z));
        } while (--rounds != 0);
    }
    
    // valor hash inicial
    private static int[] iv() {
        int[] iv = {
            0x31ba1424, 0xf7a3a62f, 0x0f6d9407, 0xc12060d1,
            0x4cd238c8, 0xd0d6dc1d, 0xda463249, 0x7d2ada01,
            0xdf87bc08, 0xb81d964f, 0xd08ee4f8, 0x1e5a1917,
            0x12577cf1, 0x59ef72d8, 0xabd47810, 0x67d263ed
        };
        return iv;
    }
    
    // completar o valor de a para ser múltiplo de b
    private static int completar_multiplo(int a, int b) {
        return b * ((a + b - 1) / b);
    }
}
