package de.unioninvestment.crud2go.spi.security.pgp;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;

public class Main {

	private static final String FILENAME_SECRETKEY = "secret.asc";
	private static final String FILENAME_PUBLICKEY = "public.asc";
	private static final String FILENAME_RESULT = "result.asc";

	public static void main(String[] args) {
		CommandLineParser clp = new BasicParser();
		try {
			CommandLine cl = clp.parse(useOptions(), args);

			if (args.length == 0 || cl.hasOption("h")) {
				throw new ParseException("print help message");
			}

			if (cl.hasOption("c")) {
				createKeyPair(cl);
			}

			if (cl.hasOption("d")) {
				decrypt(cl);
			}

			if (cl.hasOption("e")) {
				encrypt(cl);
			}
		} catch (ParseException ex) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("union-security", useOptions(), true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static Options useOptions() {
		Options options = new Options();
		options.addOption("h", "help", false, "print this message");
		options.addOption(
				"c",
				"createRSAKey",
				false,
				"create a new RSA key pair. The keys are placed in the files pub.asc and secret.asc.");
		options.addOption("s", "strength", true, "strength of new key pair");
		options.addOption("i", "identity", true, "identity of new key pair");
		options.addOption("d", "decrypt", false, "do decryption");
		options.addOption("e", "encrypt", false, "do encryption");
		options.addOption("m", "message", true,
				"the message to encrypt or filename of message to decrypt");
		options.addOption(OptionBuilder
				.withLongOpt("keys")
				.hasArgs()
				.withDescription(
						"system-dependent file names of public/private keys")
				.create("k"));
		return options;
	}

	private static void createKeyPair(CommandLine cl) throws Exception {
		System.out.println("Start creating key pair...");
		PGPCryptoUtil.exportRSAKeyPair(
				new FileOutputStream(FILENAME_SECRETKEY), new FileOutputStream(
						FILENAME_PUBLICKEY), Integer.parseInt(cl
						.getOptionValue("s")), cl.getOptionValue("i"));
	}

	private static void decrypt(CommandLine cl) throws Exception {
		System.out.println("Start decrypting message...");
		PGPSecretKeyRingCollection secretKeys = new PGPKeyContainer(Utils.read(
				new FileInputStream(cl.getOptionValue("k"))).toByteArray())
				.getSecretKeyRingCollection();
		byte[] message = Utils
				.read(new FileInputStream(cl.getOptionValue("m")))
				.toByteArray();
		byte[] decMessage = PGPCryptoUtil.decrypt(message, secretKeys,
				PGPCryptoUtil.EMPTY_PASSPHRASE);
		System.out.println("Decrypted Result:");
		System.out.println(new String(decMessage));
	}

	private static void encrypt(CommandLine cl) throws Exception {
		System.out.println("Start encrypting message...");
		List<PGPPublicKey> publicKeys = new ArrayList<PGPPublicKey>();
		for (String key : cl.getOptionValues("k")) {
			publicKeys.add(new PGPKeyContainer(Utils.read(
					new FileInputStream(key)).toByteArray()).getPublicKey());
		}
		byte[] message = cl.getOptionValue("m").getBytes();
		byte[] encMessage = PGPCryptoUtil.encrypt(message,
				publicKeys.toArray(new PGPPublicKey[publicKeys.size()]),
				SymmetricKeyAlgorithmTags.AES_256);
		System.out.println("Encrypted Result:");
		System.out.println(new String(encMessage));
		FileOutputStream fos = new FileOutputStream(FILENAME_RESULT);
		fos.write(encMessage);
		fos.close();
	}
}
