package de.uit.eai.portal;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import de.uit.eai.portal.mvn.plugin.SourceTransformer;

@Mojo(name = "merge", aggregator = true)
@Execute(goal = "merge", phase = LifecyclePhase.GENERATE_SOURCES)
public class SourceTranformerMojo extends AbstractMojo {

	@Parameter(defaultValue = "${basedir}/src/config/")
	private File sourceDirectory;

	@Parameter(defaultValue = "${project.build.directory}/combined/")
	private File outputDirectory;

	interface FileProcessor {
		void processFile(File file);
	}

	public void execute() throws MojoExecutionException {
        getLog().info("Source Directory:" + sourceDirectory);
        getLog().info("Output Directory:" + outputDirectory);

		final File outputDir = outputDirectory;

		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		if (!outputDir.isDirectory()) {
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
		traverseFiles(sourceDirectory, "xml", new FileProcessor() {
			public void processFile(File file) {
				try {
                    String outputFilePath = SourceTransformer
                            .constructAbsolutePath(outputDir.getAbsolutePath(),
                                    SourceTransformer.getRelativePath(
                                            sourceDirectory, file));
                    SourceTransformer.RESULT result = new SourceTransformer().processFile(
                            file.getCanonicalPath(), outputFilePath);
                    switch(result){
                        case NOT_PORTLET:
                            getLog().info("File "+file + " was skipped, as its not a Crud2Go-portlet file");
                            break;
                        case PORTLET_NO_PROCESSING:
                            getLog().info("File "+file + " was copied to "+outputFilePath+" as is, as its does not contains includes");
                            break;
                        case PORTLET_PROCESSING_DONE:
                            getLog().info("File "+file + " was processed, as its contains includes. Results is stored "+ outputFilePath);
                            break;
                    }
                } catch (IOException e) {
					errors.add(MessageFormat.format(
							"Fehler waehrend Bearbeitung der Datei {0}: {1}",
							file.getAbsolutePath(), e.getMessage()));
				}
			}
		});
		if (!errors.isEmpty()) {
			for (String error : errors) {
				getLog().error(error);
			}
			throw new MojoExecutionException(
					"Es gibt Fehler waehrend der Transformation von Portlet-Datein");
		}

	}

	public static void traverseFiles(File dir, String pattern,
			FileProcessor fileProcessor) {
		File listFile[] = dir.listFiles();
		if (listFile != null) {
            for (File aListFile : listFile) {
                if (aListFile.isDirectory()) {
                    traverseFiles(aListFile, pattern, fileProcessor);
                } else {
                    if (aListFile.getName().endsWith(pattern)) {
                        fileProcessor.processFile(aListFile);
                    }
                }
            }
		}
	}
}
