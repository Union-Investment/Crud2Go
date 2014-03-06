/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package de.unioninvestment.crud2go.spi.security.pgp;

import java.io.File;
import java.io.FileInputStream;

import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.slf4j.LoggerFactory;

import de.unioninvestment.crud2go.spi.security.Cryptor;
import de.unioninvestment.crud2go.spi.security.DecryptionException;
import de.unioninvestment.crud2go.spi.security.EncryptionException;

public class PGPCryptor implements Cryptor {

	private static final org.slf4j.Logger LOGGER = LoggerFactory
			.getLogger(PGPCryptor.class);

	private static final String TEXT_ENCODING = "UTF-8";

	private PGPSecretKeyRingCollection secretKeys;

	private String secretKeyFileName;

	private PGPPublicKey publicKey;

	public PGPCryptor(String secretKeyFileName) {
		this.secretKeyFileName = secretKeyFileName;
		initialize();
	}

	public void initialize() {
		if (secretKeyFileName != null && new File(secretKeyFileName).exists()) {
			try {
				// TODO handle non-existing file (degraded mode)
				FileInputStream stream = new FileInputStream(secretKeyFileName);
				byte[] keyFileBytes = Utils.read(stream).toByteArray();
				secretKeys = new PGPKeyContainer(keyFileBytes)
						.getSecretKeyRingCollection();
				publicKey = new PGPKeyContainer(keyFileBytes).getPublicKey();
				return;

			} catch (Exception e) {
				LOGGER.warn(
						"Error initializing PGP decryption - feature not supported",
						e);
			}
		} else {
			LOGGER.warn("Error initializing PGP decryption - feature not supported (key file not configured or does not exist)");
		}

	}

	@Override
	public String encrypt(String toEncrypt) {
		try {
			byte[] message = toEncrypt.getBytes(TEXT_ENCODING);
			byte[] encMessage = PGPCryptoUtil.encrypt(message,
					new PGPPublicKey[] { publicKey },
					SymmetricKeyAlgorithmTags.AES_256);
			return new String(encMessage, "ascii");

		} catch (Exception e) {
			throw new EncryptionException("error during PGP encrypting", e);
		}
	}

	@Override
	public String decrypt(String encryptedString) {
		try {
			byte[] message = encryptedString.getBytes("ascii");
			byte[] decMessage = PGPCryptoUtil.decrypt(message, secretKeys,
					PGPCryptoUtil.EMPTY_PASSPHRASE);
			return new String(decMessage, TEXT_ENCODING);

		} catch (Exception e) {
			throw new DecryptionException("error during PGP decryption", e);
		}
	}

}
