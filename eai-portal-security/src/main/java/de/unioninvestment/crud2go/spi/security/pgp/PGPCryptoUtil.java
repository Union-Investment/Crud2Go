package de.unioninvestment.crud2go.spi.security.pgp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Date;
import java.util.Iterator;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPUtil;

public final class PGPCryptoUtil {

	public static final char[] EMPTY_PASSPHRASE = new char[0];
	private static final int BUFFER_SIZE = 1 << 16;

	static {
		if (Security.getProvider(PGPUtil.getDefaultProvider()) == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
	}

	public static void exportRSAKeyPair(OutputStream secretFile,
			OutputStream publicFile, int strength, String identity)
			throws GeneralSecurityException, PGPException, IOException {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA",
				PGPUtil.getDefaultProvider());
		kpg.initialize(strength);
		KeyPair kp = kpg.generateKeyPair();

		PGPSecretKey secretKey = new PGPSecretKey(
				PGPSignature.DEFAULT_CERTIFICATION, PGPPublicKey.RSA_GENERAL,
				kp.getPublic(), kp.getPrivate(), new Date(), identity,
				PGPEncryptedData.CAST5, EMPTY_PASSPHRASE, null, null,
				new SecureRandom(), "BC");
		PGPPublicKey publicKey = secretKey.getPublicKey();

		OutputStream secretPublicOut = new ArmoredOutputStream(secretFile);
		publicKey.encode(secretPublicOut);
		secretPublicOut.close();

		OutputStream secretOut = new ArmoredOutputStream(secretFile);
		secretKey.encode(secretOut);
		secretOut.close();

		OutputStream publicOut = new ArmoredOutputStream(publicFile);
		publicKey.encode(publicOut);
		publicOut.close();
	}

	public static byte[] decrypt(byte[] message,
			PGPSecretKeyRingCollection secretKeys, char[] passphrase)
			throws GeneralSecurityException, PGPException, IOException {
		return internalDecrypt(message, secretKeys, passphrase);
	}

	public static byte[] internalDecrypt(byte[] message,
			PGPSecretKeyRingCollection secretKeys, char[] passphrase)
			throws GeneralSecurityException, PGPException, IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		InputStream in = PGPUtil.getDecoderStream(new ByteArrayInputStream(
				message));

		PGPObjectFactory factory = new PGPObjectFactory(in);
		PGPEncryptedDataList enc;

		Object object = factory.nextObject();
		if (object instanceof PGPEncryptedDataList) {
			enc = (PGPEncryptedDataList) object;
		} else {
			enc = (PGPEncryptedDataList) factory.nextObject();
		}

		Iterator it = enc.getEncryptedDataObjects();
		PGPPublicKeyEncryptedData encryptedData = null;
		PGPPrivateKey privateKey = null;

		while (privateKey == null && it.hasNext()) {
			encryptedData = (PGPPublicKeyEncryptedData) it.next();
			PGPSecretKey secretKey = secretKeys.getSecretKey(encryptedData
					.getKeyID());
			if (secretKey != null) {
				privateKey = secretKey.extractPrivateKey(passphrase,
						PGPUtil.getDefaultProvider());
			}
		}
		if (privateKey == null) {
			throw new IllegalArgumentException(
					"Private key for message not found!");
		}

		InputStream clear = encryptedData.getDataStream(privateKey,
				PGPUtil.getDefaultProvider());
		factory = new PGPObjectFactory(clear);

		object = factory.nextObject();
		if (object instanceof PGPCompressedData) {
			factory = new PGPObjectFactory(
					((PGPCompressedData) object).getDataStream());
		}

		while ((object = factory.nextObject()) != null) {
			if (object instanceof PGPLiteralData) {
				out = Utils.read(((PGPLiteralData) object).getDataStream());
			}
		}

		return out.toByteArray();
	}

	public static byte[] encrypt(byte[] message, PGPPublicKey[] publicKeys,
			int cipher) throws GeneralSecurityException, PGPException,
			IOException {
		return encrypt(message, publicKeys, cipher, null);
	}

	public static byte[] encrypt(byte[] message, PGPPublicKey[] publicKeys,
			int cipher, String filename) throws GeneralSecurityException,
			PGPException, IOException {
		return internalEncrypt(message, publicKeys, cipher, filename);
	}

	private static byte[] internalEncrypt(byte[] message,
			PGPPublicKey[] publicKeys, int cipher, String filename)
			throws GeneralSecurityException, PGPException, IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		OutputStream amoOut = new ArmoredOutputStream(out);

		PGPEncryptedDataGenerator encDataGen = new PGPEncryptedDataGenerator(
				cipher, true, new SecureRandom(), PGPUtil.getDefaultProvider());
		for (PGPPublicKey publicKey : publicKeys) {
			encDataGen.addMethod(publicKey);
		}
		OutputStream encOut = encDataGen.open(amoOut, new byte[BUFFER_SIZE]);

		PGPCompressedDataGenerator comDataGen = new PGPCompressedDataGenerator(
				CompressionAlgorithmTags.ZIP);
		OutputStream comOut = comDataGen.open(encOut, new byte[BUFFER_SIZE]);

		PGPLiteralDataGenerator litDataGen = new PGPLiteralDataGenerator();
		OutputStream litOut = litDataGen.open(comOut,
				PGPLiteralDataGenerator.BINARY, (filename != null ? filename
						: PGPLiteralDataGenerator.CONSOLE),
				PGPLiteralDataGenerator.NOW, new byte[BUFFER_SIZE]);

		litOut.write(message);

		litDataGen.close();
		comDataGen.close();
		encDataGen.close();
		amoOut.close();

		return out.toByteArray();
	}
}
