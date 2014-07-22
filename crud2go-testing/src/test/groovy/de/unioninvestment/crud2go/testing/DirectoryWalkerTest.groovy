package de.unioninvestment.crud2go.testing;


import org.junit.Test

public class DirectoryWalkerTest {

	@Test
	void testWalking(){
		println new File(".").getAbsolutePath()
		
		def fileList = []
		new DirectoryWalkerProvider("src/test/resources/de/unioninvestment/crud2go/testing").each{obj -> 
			fileList << obj
		}
		
		assert fileList.size() >= 2
	}
}
