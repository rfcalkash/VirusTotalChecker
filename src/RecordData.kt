import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import com.beust.klaxon.*
import java.io.InputStream


data class JSONParseResult(var Results:List<ScanResult>,var response_code:Int, var Total_scanners:Int, var Positives:Int){}

data class ScanResult(var Antivirus:String="", var IsPositive:Boolean=false, var FoundMalware:String=""){}

data class RecordData(var SourceFileName:String="", var AdditionalData:String="", var md5:String="", var sha1:String="", var Result:JSONParseResult=JSONParseResult(emptyList(),0,0,0), var Scanned:Boolean=false) {

    val API_key:String="b4c3c5d95147898a48f2944cd563a6464a7dd36c2c2e177e806737b8f9fd6a1a"

    fun AddData(x: Any){
        if(x is String)
        {
            var to_proceed=(x as String).trim()
            val md5reg=Regex("([0-9]|[A-F]|[a-f]){32,32}")
            val sha1reg=Regex("([0-9]|[A-F]|[a-f]){40,40}")
            if(md5reg.matches(to_proceed)) md5=to_proceed
            else if(sha1reg.matches(to_proceed)) sha1=to_proceed
            else AdditionalData+=to_proceed+" "
        }
    }

    suspend fun CheckData(){
        var res:String
        if(md5!="") res=md5
        else if(sha1!="") res=sha1
        else res=""
        if(res!="") {
            val url = "https://www.virustotal.com/vtapi/v2/file/report?apikey=$API_key&resource=$res"
            val obj = URL(url)
            var response_code:Int=0
            val response = StringBuffer()

            with(obj.openConnection() as HttpURLConnection) {
                // optional default is GET
                requestMethod = "GET"
                println("\nSending 'GET' request to URL : $url")
                response_code=responseCode
                println("Response Code : $responseCode")
                val parser: Parser = Parser()
                val json:JsonObject=parser.parse(inputStream) as JsonObject
                if(response_code==200) {
                    Result = JSONToScanResults(json)
                    if(Result.response_code==1) Scanned=true
                }
            }


        }
    }

    fun JSONToScanResults(json:JsonObject):JSONParseResult{
        val resp_code:Int=json.get("response_code") as Int
        var total: Int=0
        var positives: Int=0
        var scan_list= mutableListOf<ScanResult>()
        if(resp_code==1) {
            total= json.get("total") as Int
            positives= json.get("positives") as Int
            var scans=json.get("scans") as JsonObject
            scans.forEach {
                var data=it.value as JsonObject
                if(data.get("detected") as Boolean) {
                    var scan_data=ScanResult(it.key,true,data.get("result") as String)
                    scan_list.add(scan_data)
                }
            }

        }
        return JSONParseResult(scan_list,resp_code,total,positives)
    }

}