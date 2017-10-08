data class ScanResult(var Antivirus:String="",var ScanResult:Boolean=false,var FoundMalware:String=""){}

data class RecordData(var SourceFileName:String="", var AdditionalData:String="",var md5:String="",var sha1:String="", var Results:List<ScanResult>, var Scanned:Boolean=false) {

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
        
    }

}