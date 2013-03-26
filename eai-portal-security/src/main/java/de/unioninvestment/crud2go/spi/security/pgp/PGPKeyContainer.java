package de.unioninvestment.crud2go.spi.security.pgp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;

public final class PGPKeyContainer {

    private final PGPPublicKey publicKey;
    private final PGPSecretKeyRingCollection secretKeyRingCollection;

    public PGPKeyContainer(byte[] input) throws PGPException, IOException {
        InputStream in = new ByteArrayInputStream(input);
        publicKey = readPublicKey(PGPUtil.getDecoderStream(in));
        secretKeyRingCollection = readSecretKeyRingCollection(PGPUtil.getDecoderStream(in));
    }

    public PGPPublicKey getPublicKey() {
        if (publicKey != null) {
            return publicKey;
        }

        throw new IllegalArgumentException("Can't find or read public key.");
    }

    public PGPSecretKeyRingCollection getSecretKeyRingCollection() {
        if (secretKeyRingCollection != null) {
            return secretKeyRingCollection;
        }

        throw new IllegalArgumentException("Can't find or read secret key ring collection.");
    }

    private PGPPublicKey readPublicKey(InputStream in) throws IOException, PGPException {
        PGPPublicKeyRingCollection pgpPub = null;
        try {
            pgpPub = new PGPPublicKeyRingCollection(in);
        } catch (PGPException ex) {
            return null;
        }
        Iterator rIt = pgpPub.getKeyRings();
        while (rIt.hasNext()) {
            PGPPublicKeyRing kRing = (PGPPublicKeyRing) rIt.next();
            Iterator kIt = kRing.getPublicKeys();
            while (kIt.hasNext()) {
                PGPPublicKey k = (PGPPublicKey) kIt.next();
                if (k.isEncryptionKey()) {
                    return k;
                }
            }
        }
        return null;
    }

    private PGPSecretKeyRingCollection readSecretKeyRingCollection(InputStream in) throws IOException, PGPException {
        PGPSecretKeyRingCollection pgpSec = null;
        try {
            pgpSec = new PGPSecretKeyRingCollection(in);
        } catch (PGPException ex) {
            return null;
        }
        return pgpSec;
    }
}
