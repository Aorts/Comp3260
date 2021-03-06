package au.com.uon.comp3260;

import java.nio.file.Path;

import au.com.uon.comp3260.util.Helper;
import au.com.uon.comp3260.util.OutputFileWriter;

/**
 * 
 * @author Felix Behrendt & Andrew Thursby
 * 
 */
public class Decrypter {

    public void decrypt(Path inputFile, Path outputFile) {
        decrypt(Helper.readCipherText(inputFile), Helper.readKey(inputFile),
                outputFile);
    }

    public String decrypt(String cipherText, String key, Path outputFile) {
        long start = System.currentTimeMillis();
        byte[] cipherTextByteArray = Helper.stringToByteArray(cipherText);
        byte[] keyBytes = Helper.stringToByteArray(key);
        byte[][] decrypted = decryptByteArray(cipherTextByteArray, keyBytes);
        long end = System.currentTimeMillis();

        String plainText = Helper.arrayToString(
                Helper.matrixToArray(decrypted), false);

        if (outputFile != null) {
            OutputFileWriter writer = new OutputFileWriter(outputFile);
            writer.setKey(key);
            writer.setPlainText(plainText);
            writer.setDecryption(true);
            writer.setStartTime(start);
            writer.setEndTime(end);
            writer.setCipherText(cipherText);
            writer.writeToFile();
        }
        return plainText;
    }

    public byte[][] decryptByteArray(byte[] cipherTextByteArray, byte[] key) {
        byte[][] input = Helper.arrayToMatrix(cipherTextByteArray);
        AddRoundKey roundKeyAdder = new AddRoundKey(key, true);
        SubstituteBytes byteSubstituter = new SubstituteBytes();
        ShiftRows rowShifter = new ShiftRows();
        MixColumns columnMixer = new MixColumns(true);

        byte[][] output = Helper.copyByteMatrix(input);
        for (int round = 0; round < 10; round++) {
            output = Helper.copyByteMatrix(roundKeyAdder.addRoundKey(output,
                    round));
            if (round > 0) {
                output = Helper.copyByteMatrix(columnMixer.mixColumns(output));
            }
            output = Helper.copyByteMatrix(rowShifter.shiftRows(output, true));
            output = Helper.copyByteMatrix(byteSubstituter.substituteBytes(
                    output, true));
        }
        output = Helper.copyByteMatrix(roundKeyAdder.addRoundKey(output, 10));
        return output;
    }

}
