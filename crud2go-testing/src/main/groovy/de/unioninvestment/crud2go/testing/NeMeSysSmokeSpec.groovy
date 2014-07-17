package de.unioninvestment.crud2go.testing

import spock.lang.*
//import org.springframework.test.context.ContextConfiguration


//@ContextConfiguration("/eai-portal-config-test-applicationcontext.xml")
class NeMeSysSmokeSpec extends CrudSpec {
	
	@Shared
//	def pathes = ["NeMeSys/./Aufgaben.xml",]
	
		def pathes = ["NeMeSys/./Aufgaben.xml",
					'NeMeSys/./Logfiles.xml',
					'NeMeSys/awv/DatenBerechnen.xml',
					'NeMeSys/awv/DatenExport.xml',
					'NeMeSys/awv/DatenFreigabe.xml',
					'NeMeSys/awv/archiv/Daten_Archiv.xml',
					'NeMeSys/awv/archiv/reportingdaten/AWV_Reportingdaten_Archiv.xml',
					'NeMeSys/awv/archiv/rohdaten/AWV_Zahlungen_Archiv.xml',
					'NeMeSys/awv/daska/Daska_Log.xml',
					'NeMeSys/awv/daska/Daska_Upload.xml',
					'NeMeSys/awv/fondsgruppen/AWV_Fondsgruppen.xml',
					'NeMeSys/awv/fondsgruppen/AWV_Fondsgruppen_Fonds.xml',
					'NeMeSys/awv/fondsgruppen/AWV_Fondsgruppen_Freigabe.xml',
					'NeMeSys/awv/fondsgruppen/AWV_Fondsgruppen_Reports.xml',
					'NeMeSys/awv/fondsgruppen/Geplante_Tasks.xml',
					//braucht jetzt zu viel Zeit
					//'NeMeSys/awv/parameter/AWV_Auswertungen.xml',
					//'NeMeSys/awv/parameter/AWV_Auswertungen_code.xml',
					//'NeMeSys/awv/parameter/AWV_Auswertungen_gruppe.xml',
					'NeMeSys/awv/parameter/AWV_Definitionen.xml',
					'NeMeSys/awv/parameter/AWV_Definitionen_Freigabe.xml',
					'NeMeSys/awv/parameter/AWV_Depotbanken.xml',
					'NeMeSys/awv/parameter/AWV_Depotbanken_Freigabe.xml',
					'NeMeSys/awv/parameter/AWV_Depotbanken_Map.xml',
					'NeMeSys/awv/parameter/AWV_Depotbanken_Map_Freigabe.xml',
					'NeMeSys/awv/parameter/AWV_Mappings.xml',
					'NeMeSys/awv/parameter/AWV_Mappings_Freigabe.xml',
					'NeMeSys/awv/reportingdaten/Reportingdaten.xml',
					//jetzt kann nicht geladen sein
					//'NeMeSys/awv/reportingdaten/Reportingdaten_Freigabe.xml',
					'NeMeSys/awv/rohdaten/AWV_Buchungen.xml',
					'NeMeSys/awv/rohdaten/AWV_Buchungen_Freigabe.xml',
					'NeMeSys/awv/rohdaten/AWV_Buchungen_Storno.xml',
					'NeMeSys/awv/rohdaten/AWV_Buchungen_Storno_Freigabe.xml',
					'NeMeSys/awv/rohdaten/AWV_Zahlungen.xml',
					'NeMeSys/awv/rohdaten/AWV_Zahlungen_Freigabe.xml',
					'NeMeSys/awv/rohdaten/Manuelle_Buchungen.xml',
					'NeMeSys/awv/rohdaten/Manuelle_Buchungen_Uebersicht.xml',
					]
	
	static expectedMissingSearchColumns = [
		"NeMeSys/./Logfiles.xml":['SEARCH_MODULE'],
		"NeMeSys/awv/archiv/Daten_Archiv.xml":['KOFG_ID'],
		"NeMeSys/awv/parameter/AWV_Depotbanken.xml":['S_GUELTIG_BIS', 'NAME_DEPOTBANK'],
		'NeMeSys/bclstatistik/parameter/BCL_Definitionen.xml':['S_GUELTIG_BIS'],
		'NeMeSys/bclstatistik/parameter/BCL_Mappings.xml':['S_GUELTIG_BIS'],
		'NeMeSys/bclstatistik/Statuskontrolle.xml':['FONDS_GROUP'],
		'NeMeSys/bclstatistik/archiv/reportingdaten/S_1.6_Bereinigung_NGF.xml':['CSSF_NUMMER', 'IML_FUND_NUMMER'],
		'NeMeSys/bubastatistik/DatenFreigeben.xml':['FONDS_GROUP'],
		'NeMeSys/bubastatistik/archiv/reportingdaten/bilanz_gf_10393/BilanzSicht.xml':['REPORT_MONAT'],
		'NeMeSys/bubastatistik/archiv/reportingdaten/bilanz_ngf_10391/Bilanzsicht.xml':['REPORT_MONAT'],
		'NeMeSys/bubastatistik/reportingdaten/bilanz_gf_10393/Bilanzsicht.xml':['REPORT_MONAT'],
		'NeMeSys/bubastatistik/reportingdaten/bilanz_ngf_10391/Bilanzsicht.xml':['REPORT_MONAT'],
		'NeMeSys/bvistatistik/Statuskontrolle.xml':['FONDS_GROUP'],
		'NeMeSys/bvistatistik/archiv/reportingdaten/Einzelmeldung_Ausschuettungen.xml':['REPORT_MONAT'],
		'NeMeSys/bvistatistik/archiv/reportingdaten/Einzelmeldung_Drittmandate_Ausschuettungen.xml':['REPORT_MONAT'],
	]

	private void testSimpleMethod(String path){
		println "SIMPLE METHOD TEST ${path}"
		where:
			path << pathes
	}
		
	@Ignore
	@Unroll
	def "checkQueries #path"() {
		given:
			load(path)
		expect:
		 	portlet != null
			ScriptPortletChecks.checkOptionsInScriptPortlet(scriptPortlet) 
		where:
		    path << pathes	 
	}
	
	@Ignore
	@Unroll
	def "checkSearchColumns #path"(){
		given:
			load(path)
		expect:
			portlet != null
			PortletConfigChecks.checkSearchColumns(path, portletConfig, expectedMissingSearchColumns)
		where:
			path << pathes
	}
	
	@Ignore
	@Unroll
	def "checkSearchActions #path"(){
		given:
			load(path)
		expect:
			portlet != null
			ScriptPortletChecks.checkSearchActions(path, scriptPortlet)
		where:
			path << pathes
	}
}

