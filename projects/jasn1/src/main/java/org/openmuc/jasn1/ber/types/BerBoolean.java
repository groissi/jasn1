/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.openmuc.jasn1.ber.types;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import org.openmuc.jasn1.ber.BerLength;
import org.openmuc.jasn1.ber.BerTag;
import org.openmuc.jasn1.ber.ReverseByteArrayOutputStream;

public class BerBoolean implements Serializable, BerType {

    private static final long serialVersionUID = 1L;

    public final static BerTag tag = new BerTag(BerTag.UNIVERSAL_CLASS, BerTag.PRIMITIVE, BerTag.BOOLEAN_TAG);

    public byte[] code = null;

    public boolean value;

    public BerBoolean() {
    }

    public BerBoolean(byte[] code) {
        this.code = code;
    }

    public BerBoolean(boolean value) {
        this.value = value;
    }

    @Override
    public int encode(OutputStream reverseOS) throws IOException {
        return encode(reverseOS, true);
    }

    public int encode(OutputStream reverseOS, boolean withTag) throws IOException {

        if (code != null) {
            for (int i = code.length - 1; i >= 0; i--) {
                reverseOS.write(code[i]);
            }
            if (withTag) {
                return tag.encode(reverseOS) + code.length;
            }
            return code.length;
        }

        int codeLength = 1;

        if (value) {
            reverseOS.write(0x01);
        }
        else {
            reverseOS.write(0);
        }

        codeLength += BerLength.encodeLength(reverseOS, codeLength);

        if (withTag) {
            codeLength += tag.encode(reverseOS);
        }

        return codeLength;
    }

    @Override
    public int decode(InputStream is) throws IOException {
        return decode(is, true);
    }

    public int decode(InputStream is, boolean withTag) throws IOException {

        int codeLength = 0;

        if (withTag) {
            codeLength += tag.decodeAndCheck(is);
        }

        BerLength length = new BerLength();
        codeLength += length.decode(is);

        if (length.val != 1) {
            throw new IOException("Decoded length of BerBoolean is not correct");
        }

        int nextByte = is.read();
        if (nextByte == -1) {
            throw new EOFException("Unexpected end of input stream.");
        }

        codeLength++;
        if (nextByte == 0) {
            value = false;
        }
        else {
            value = true;
        }

        return codeLength;
    }

    public void encodeAndSave(int encodingSizeGuess) throws IOException {
        ReverseByteArrayOutputStream os = new ReverseByteArrayOutputStream(encodingSizeGuess);
        encode(os, false);
        code = os.getArray();
    }

    @Override
    public String toString() {
        return "" + value;
    }
}
