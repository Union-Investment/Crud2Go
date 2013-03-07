package de.unioninvestment.crud2go.spi.security.pgp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;

import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PGPCryptoUtilTest {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PGPCryptoUtilTest.class);

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
		try {

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
		} catch (Exception e) {
			warnOnKeySizeException(e);
		}

	}

	private void warnOnKeySizeException(Exception e) throws Exception {
		Throwable cause = e.getCause();
		while (cause != null) {
			if (cause instanceof InvalidKeyException) {
				LOGGER.warn("It seems that the strong encryption extension is not installed - no failure");
				return;
			}
			cause = cause.getCause();
		}
		throw e;
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testEncryptDecryptElgamal() throws Exception {
		try {
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
		} catch (Exception e) {
			warnOnKeySizeException(e);
		}
	}

	@Test
	public void testEncryptDecryptRSA() throws Exception {
		try {
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
		} catch (Exception e) {
			warnOnKeySizeException(e);
		}
	}

	@Test
	public void testEncryptDecryptElgamalVsRSA() throws Exception {
		byte[] encMessage;
		try {
			encMessage = PGPCryptoUtil.encrypt(message.getBytes(),
					new PGPPublicKey[] { keyPair1.getPublicKey() }, cipher);
			PGPCryptoUtil.decrypt(encMessage,
					keyPair2.getSecretKeyRingCollection(),
					PGPCryptoUtil.EMPTY_PASSPHRASE);
			fail("Expected IllegalArgumentException");

		} catch (IllegalArgumentException e) {
			// expected

		} catch (Exception e) {
			warnOnKeySizeException(e);
		}
	}

	@Test
	public void testEncryptDecrypt() throws Exception {
		try {
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
		} catch (Exception e) {
			warnOnKeySizeException(e);
		}
	}

}
