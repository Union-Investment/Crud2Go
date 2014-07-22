package de.unioninvestment.crud2go.testing

import java.util.Iterator;

class DirectoryWalkerProvider implements Iterable{

	private String baseDirPath
	private def nameFilter
	
	public DirectoryWalkerProvider(String baseDir, Object nameFilter = ~/.*.xml/) {
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
		baseDir.eachFileMatch(nameFilter){  file ->
			fileList << file.getAbsolutePath()
		}
		//matching files in subdirectories
		baseDir.eachDirRecurse { dir ->
			dir.eachFileMatch(nameFilter) { file ->
				fileList << file.getAbsolutePath()
			}
		}
		return fileList.iterator();
	}

}
