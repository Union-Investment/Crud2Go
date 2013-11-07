package de.unioninvetsment.eai.portal.crud2go.mvn;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "ccScript")
public class ScriptMavenPlugin extends AbstractMojo {

	public ScriptMavenPlugin() {

	}

	public void execute() ) throws MojoExecutionException{
		getLog().info("Hello, world.");
	}

}
