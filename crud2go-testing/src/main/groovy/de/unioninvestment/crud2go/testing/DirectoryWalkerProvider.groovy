package de.unioninvestment.crud2go.testing

import groovy.transform.CompileStatic

import java.util.Iterator
import java.util.regex.Pattern;

@CompileStatic
class DirectoryWalkerProvider implements Iterable{

	private String baseDirPath
	private Pattern nameFilter
	
	public DirectoryWalkerProvider(String baseDir, Pattern nameFilter = ~/.*.xml/) {
		super();
		this.baseDirPath = baseDir;
		this.nameFilter = nameFilter;
	}

	@Override
	public Iterator iterator() {
		def fileList = []
		def absBaseDirPath = new File(baseDirPath).getAbsolutePath()
		def baseDir = new File(absBaseDirPath)
		//matching files in currentDir
		baseDir.eachFileMatch(nameFilter){ File file ->
			fileList << file.absolutePath
		}
		//matching files in subdirectories
		baseDir.eachDirRecurse { File dir ->
			dir.eachFileMatch(nameFilter) { File file ->
				fileList << file.absolutePath
			}
		}
		return fileList.iterator();
	}

}
