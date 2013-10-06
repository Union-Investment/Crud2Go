package de.uit.eai.portal;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.Parameter;

import de.uit.eai.portal.mvn.plugin.SourceTransformer;

@Mojo(name = "merge", aggregator = true)
@Execute(goal = "merge", phase = LifecyclePhase.GENERATE_SOURCES)
public class SourceTranformerMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project.build.sourceDirectory}")
	private File sourceDirectory;

	@Parameter(defaultValue = "${project.build.directory}")
	private File outputDirectory;

	interface FileProcessor {
		void processFile(File file);
	}

	public void execute() throws MojoExecutionException {
		final File f = outputDirectory;

		if (!f.exists()) {
			f.mkdirs();
		}
		if (!f.isDirectory()) {
			throw new MojoExecutionException(
					"outputDirect Parameter ist nicht ein Verzeichnis!"
							+ outputDirectory);
		}

		if (!sourceDirectory.isDirectory()) {
			getLog().error(
					"sourceDirectory Parameter ist nicht ein Verzeichnis!"
							+ sourceDirectory);

			throw new MojoExecutionException(
					"sourceDirectory Parameter ist nicht ein Verzeichnis!"
							+ sourceDirectory);
		}

		final List<String> errors = new ArrayList<String>();
		traverseFiles(sourceDirectory, "*.xml", new FileProcessor() {
			public void processFile(File file) {
				// TODO: remove
				System.out.println(file.getAbsolutePath());
				try {
                    SourceTransformer.RESULT result = new SourceTransformer().processFile(
                            file.getCanonicalPath(), SourceTransformer
                            .constructAbsolutePath(f.getAbsolutePath(),
                                    SourceTransformer.getRelativePath(
                                            sourceDirectory, file)));
                    switch(result){
                        case NOT_PORTLET:
                            getLog().info("File "+file + " was skipped, as its not a Crud2Go-portlet file");
                            break;
                        case PORTLET_NO_PROCESSING:
                            getLog().info("File "+file + " was copyied as is, as its does not contains includes");
                            break;
                        case PORTLET_PROCESSING_DONE:
                            getLog().info("File "+file + " was processed, as its contains includes");
                            break;
                    }
                } catch (IOException e) {
					errors.add(MessageFormat.format(
							"Error while procecessing file {0}: {1}",
							file.getAbsolutePath(), e.getMessage()));
				}
			}
		});
		if (!errors.isEmpty()) {
			for (String error : errors) {
				getLog().error(error);
			}
			throw new MojoExecutionException(
					"There were failures during transofmation of portlet files");
		}

	}

	public static void traverseFiles(File dir, String pattern,
			FileProcessor fileProcessor) {
		File listFile[] = dir.listFiles();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				if (listFile[i].isDirectory()) {
					traverseFiles(listFile[i], pattern, fileProcessor);
				} else {
					if (listFile[i].getName().endsWith(pattern)) {
						fileProcessor.processFile(listFile[i]);
					}
				}
			}
		}
	}
}
