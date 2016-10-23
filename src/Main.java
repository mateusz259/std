import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.xml.bind.DatatypeConverter;
 
 
public class Main {
 
    private static String Suffiks = "e47e1024c2f32b2a643a47e95a23e6aaebf34580bbfe03096363acae";
    private static long  max = 4294967295L;
    private static String IV = "bf9416df796769c2a5bf3963f4e43322";
    private static byte[] IVBytes = DatatypeConverter.parseHexBinary(IV);
    private static String kryptogram = "b9RRhsL0duGGru1EdI+TFk02Euev1lhVuYbvx3Vhjs6R52kE3Fu+8NMQVTik6XhB";
 
 
    private static class KeyFinder implements Runnable{
 
        int id;
        long from;
        long to;
 
        PrintStream stream;
 
        public KeyFinder(int id, long from, long to) {
            this.id = id;
            this.from = from;
            this.to = to;
 
            try {
                stream = new PrintStream(new File("Wynik"+id+".txt"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
 
        @Override
        public void run() {
            stream.println("W¹tek "+id);
            stream.println("Od "+from+" do "+to);
            szukanie(from, to, stream);
        }
    }
 
 
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
 
        long range = max/4;
 
        Thread[] watki = new Thread[4];
 
        for(int i=0; i<4; i++){
            watki[i] = new Thread(new KeyFinder(i,i*range, i*range+range));
            watki[i].start();
        }
 
        for(Thread watek: watki){
            watek.join();
        }
 
 
 
    }
 
    public static void szukanie(long from, long to, PrintStream out){
        for(long i = from; i<=to; i++){
            String Prefiks = String.format("%8s", Long.toHexString(i)).replaceAll(" ","0");
            String fullKey = Prefiks+Suffiks;
 
            byte[] kluczbyte = DatatypeConverter.parseHexBinary(fullKey);
 
            Key key = new SecretKeySpec(kluczbyte, "AES");
 
 
            try {
 
 
 
                Cipher c = Cipher.getInstance("AES/CBC/NoPadding");
                c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IVBytes));
                byte[] decryptedBytes = c.doFinal(Base64.getDecoder().decode(kryptogram));
 
                boolean invalidCharacter = false;
 
 
 
                CharsetDecoder cs = Charset.forName("UTF-8").newDecoder();
 
 
                CharBuffer buffer = cs.decode(ByteBuffer.wrap(decryptedBytes));
                out.println(fullKey);
                out.println(buffer);
 
 
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            	System.out.println("B³¹d1");
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            	System.out.println("B³¹d2");
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            	System.out.println("B³¹d3");
            } catch (BadPaddingException e) {
                e.printStackTrace();
            	System.out.println("B³¹d4");
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            	System.out.println("B³¹d5");
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            	System.out.println("B³¹d6");
            } catch (CharacterCodingException e) {
            	System.out.println("B³¹d7");
            }
        }
    }
}