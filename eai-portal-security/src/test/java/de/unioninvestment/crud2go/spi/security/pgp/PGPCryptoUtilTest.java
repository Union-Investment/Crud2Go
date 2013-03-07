package de.unioninvestment.crud2go.spi.security.pgp;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;

import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.junit.Before;
import org.junit.Test;

public final class PGPCryptoUtilTest {

	private String message;
	private int cipher;
	private PGPKeyContainer keyPair1;
	private PGPKeyContainer keyPair2;

	@Before
	public void before() throws Exception {
		message = "Test message";
		cipher = SymmetricKeyAlgorithmTags.AES_256;

		keyPair1 = new PGPKeyContainer(Utils.read(
				PGPCryptoUtilTest.class
						.getResourceAsStream("/secretElgamal.asc"))
				.toByteArray());
		keyPair2 = new PGPKeyContainer(Utils.read(
				PGPCryptoUtilTest.class.getResourceAsStream("/secretRSA.asc"))
				.toByteArray());
	}

	@Test
	public void testExportRSAKeyPairAndEncryptDecrypt() throws Exception {
		ByteArrayOutputStream secretOut = new ByteArrayOutputStream();
		ByteArrayOutputStream publicOut = new ByteArrayOutputStream();
		PGPCryptoUtil.exportRSAKeyPair(secretOut, publicOut, 1024,
				"osiris@union-investment.de");

		keyPair1 = new PGPKeyContainer(publicOut.toByteArray());
		keyPair2 = new PGPKeyContainer(secretOut.toByteArray());

		byte[] encMessage, decMessage;
		encMessage = PGPCryptoUtil.encrypt(message.getBytes(),
				new PGPPublicKey[] { keyPair1.getPublicKey() }, cipher);
		System.out.println("--- ENCRYPTED RESULT NEW RSA KEY ---");
		System.out.println(new String(encMessage));
		System.out.println("--- FINISHED ---");
		decMessage = PGPCryptoUtil.decrypt(encMessage,
				keyPair2.getSecretKeyRingCollection(),
				PGPCryptoUtil.EMPTY_PASSPHRASE);
		assertEquals(message, new String(decMessage));
	}

	@Test
	public void testEncryptDecryptElgamal() throws Exception {
		byte[] encMessage, decMessage;
		encMessage = PGPCryptoUtil.encrypt(message.getBytes(),
				new PGPPublicKey[] { keyPair1.getPublicKey() }, cipher);
		System.out.println("--- ENCRYPTED RESULT Elgamal ---");
		System.out.println(new String(encMessage));
		System.out.println("--- FINISHED ---");
		decMessage = PGPCryptoUtil.decrypt(encMessage,
				keyPair1.getSecretKeyRingCollection(),
				PGPCryptoUtil.EMPTY_PASSPHRASE);
		assertEquals(message, new String(decMessage));
	}

	@Test
	public void testEncryptDecryptRSA() throws Exception {
		byte[] encMessage, decMessage;
		encMessage = PGPCryptoUtil.encrypt(message.getBytes(),
				new PGPPublicKey[] { keyPair2.getPublicKey() }, cipher);
		System.out.println("--- ENCRYPTED RESULT RSA ---");
		System.out.println(new String(encMessage));
		System.out.println("--- FINISHED ---");
		decMessage = PGPCryptoUtil.decrypt(encMessage,
				keyPair2.getSecretKeyRingCollection(),
				PGPCryptoUtil.EMPTY_PASSPHRASE);
		assertEquals(message, new String(decMessage));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEncryptDecryptElgamalVsRSA() throws Exception {
		byte[] encMessage;
		encMessage = PGPCryptoUtil.encrypt(message.getBytes(),
				new PGPPublicKey[] { keyPair1.getPublicKey() }, cipher);
		PGPCryptoUtil.decrypt(encMessage,
				keyPair2.getSecretKeyRingCollection(),
				PGPCryptoUtil.EMPTY_PASSPHRASE);
	}

	@Test
	public void testEncryptDecrypt() throws Exception {
		byte[] encMessage, decMessage;
		encMessage = PGPCryptoUtil.encrypt(
				message.getBytes(),
				new PGPPublicKey[] { keyPair1.getPublicKey(),
						keyPair2.getPublicKey() }, cipher);
		System.out.println("--- ENCRYPTED RESULT Elgamal and RSA ---");
		System.out.println(new String(encMessage));
		System.out.println("--- FINISHED ---");
		decMessage = PGPCryptoUtil.decrypt(encMessage,
				keyPair1.getSecretKeyRingCollection(),
				PGPCryptoUtil.EMPTY_PASSPHRASE);
		assertEquals(message, new String(decMessage));
		decMessage = PGPCryptoUtil.decrypt(encMessage,
				keyPair2.getSecretKeyRingCollection(),
				PGPCryptoUtil.EMPTY_PASSPHRASE);
		assertEquals(message, new String(decMessage));
	}

}
