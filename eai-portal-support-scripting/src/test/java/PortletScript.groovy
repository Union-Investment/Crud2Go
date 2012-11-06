import de.unioninvestment.eai.portal.portlet.crud.scripting.model.*;
	def test = "test"
	
    class MyCustomBackend implements ScriptContainerBackend {
    
    	Map data = [
    				1:[1,null,Date.parse("dd-MM-yyyy","31-12-2010"),null,"Hello Horst",4711,null,null,null], 
    			    2:[2,null,null,null,"Hello Peter",4712,null,null,null]
    			   ]
    	
		Closure metaData = {
			ID(type:Long,readonly:true, required:true, partOfPrimaryKey:true)
			CNUMBER5_2(type:Integer,readonly:false, required:false)
			CDATE(type:java.util.Date,readonly:false, required:false)
			CTIMESTAMP(type:java.util.Date,readonly:true, required:false)
			CVARCHAR5_NN(type:String,readonly:true, required:true)
			CNUMBER5_2_NN(type:Integer,readonly:true, required:true)
			CDATE_NN(type:java.util.Date,readonly:true, required:false)
			CTIMESTAMP_NN(type:java.util.Date,readonly:true, required:false)
			TESTDATA(type:String,readonly:true, required:false)
		}
		
		List read() {
			println data.size()
			return data.values().asList()
		}
		
		void update(List rows) {
			rows.each { row ->
				if (row.isNewItem()) {
					data.put(row.values.ID, [row.values.ID, row.values.CNUMBER5_2, row.values.CDATE,row.values.CTIMESTAMP,row.values.CVARCHAR5_NN,row.values.CNUMBER5_2_NN,row.values.CDATE_NN,row.values.CTIMESTAMP_NN,row.values.TESTDATA])
				} else if (row.isModified()) {
				  	data.put(row.values.ID, [row.values.ID, row.values.CNUMBER5_2, row.values.CDATE,row.values.CTIMESTAMP,row.values.CVARCHAR5_NN,row.values.CNUMBER5_2_NN,row.values.CDATE_NN,row.values.CTIMESTAMP_NN,row.values.TESTDATA])
				} else if (row.isDeleted()) {
					data.remove(row.values.ID)	
				}
			}
		}
	}				