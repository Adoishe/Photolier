package com.adoishe.photolier

//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.RequestBody.Companion.toRequestBody

//import okhttp3.internal.Util
import android.content.Context
import android.view.View
import android.widget.ProgressBar
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.kobjects.base64.Base64
import org.ksoap2.HeaderProperty
import org.ksoap2.SoapEnvelope
import org.ksoap2.SoapFault
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import java.io.IOException
import java.util.*


class DataLoader () {

    private var byteArrayList: MutableList<ByteArray> = ArrayList()
    private var stringArrayList: MutableList<String> = ArrayList()
//    private var protocols: MutableList<String> = ArrayList<Protocol>()//.add(Protocol.HTTP_2)
//    val clientGlobal          = OkHttpClient().protocols(Util.immutableList(Protocol.HTTP_2, Protocol.HTTP_1_1))
//    val clientGlobal          = OkHttpClient().newBuilder().protocols()
    var protocols = arrayListOf(Protocol.HTTP_2 , Protocol.HTTP_1_1)
//    val timeOut =  20
    //private val clientGlobal          = OkHttpClient().newBuilder().protocols(protocols).callTimeout(20, TimeUnit.SECONDS).build()
    private val clientGlobal          = OkHttpClient().newBuilder().protocols(protocols).build()

    private fun threadFormats(
        context: Context,
        hashArrayList: MutableList<Int>,
        sourceName: String
    ): Thread {

        return Thread {

            var res = ""

            try {

                var method = ""
                var action = ""

                when (sourceName) {
                    "Справочник.Форматы" -> {
                        method = GET_FORMATS_METHOD_NAME
                        action = GET_FORMATS_SOAP_ACTION
                    }
                    "Справочник.Материалы" -> {
                        method = GET_MATERIALS_METHOD_NAME
                        action = GET_MATERIALS_SOAP_ACTION
                    }
                    else -> {
                        method = GET_FORMATS_METHOD_NAME
                        action = GET_FORMATS_SOAP_ACTION
                    }
                }

                val request     = SoapObject(NAMESPACE, method)
                val json2send   = JSONObject()
                val hashArray   = Gson().toJson(hashArrayList)

                json2send.put("hashArray"       , hashArray)
                json2send.put("MetaDataName"    , sourceName)

                request.addProperty("jsonString", json2send.toString())
                //-----------------------------------------------------------------------------
                res = sendSoapObject(request, action)

                when (sourceName) {
                    "Справочник.Форматы" -> {

                        ImageFormat.status = ImageFormat.SYNC
                    }
                    "Справочник.Материалы" -> {

                        MaterialPhoto.status = MaterialPhoto.SYNC

                    }
                    else -> {

                    }
                }

            } catch (e: Exception) {

                e.printStackTrace()

                res = e.toString()

                when (sourceName) {
                    "Справочник.Форматы" -> {

                        ImageFormat.status = ImageFormat.SYNCERR
                        ImageFormat.syncerr = res

                    }
                    "Справочник.Материалы" -> {

                        MaterialPhoto.status = MaterialPhoto.SYNCERR
                        MaterialPhoto.syncerr = res


                    }
                    else -> {

                    }
                }
            }
            DataLoader.res = res
        }
    }

    fun syncFormats(hashArrayList: MutableList<Int>, context: Context, sourceName: String): String {

        var method          = GET_FORMATS_METHOD_NAME
        var action          = GET_FORMATS_SOAP_ACTION
        val threadFormats   = threadFormats(context, hashArrayList, sourceName)
        //val progressBar = (context as MainActivity).findViewById(R.id.progressBar) as ProgressBar

        //progressBar.visibility = ProgressBar.VISIBLE

        threadFormats.start()

        try {

            threadFormats.join()

            //progressBar.visibility = ProgressBar.INVISIBLE
            DataLoader.syncSucc = true

            return DataLoader.res

        } catch (e: InterruptedException) {

            e.printStackTrace()

            return e.toString()
        }
    }

    fun sendOrder(jsonString: String, jsonObject: JSONObject, mainAct : MainActivity): String {

        var res = ""

        try {

            val request = SoapObject(NAMESPACE, SEND_ORDER_METHOD_NAME)

            request.addProperty("ID"    , jsonString)
            request.addProperty("Num"   , jsonObject.toString())

//-----------------------------------------------------------------------------
            res = sendSoapObject(request, SEND_ORDER_ACTION)

        } catch (e: Exception) {

            e.printStackTrace()

            res = e.toString()

            mainAct.saveLog("Ошибка!---$res")

//            saveLog("sending $index img")
        }

        return res
    }


    fun sendOrderOverHTTP(jsonObjHead: JSONObject, jsonObject: JSONObject, mainAct : MainActivity): String {
        try {

//            val client          = OkHttpClient()
            val client          =  clientGlobal
            val pieceByteArray  = (jsonObject.get("mValues") as JSONObject).get("pieceOfData") as ByteArray
            //val mediaType       = "application/octet-stream".toMediaTypeOrNull()
            //val body            = pieceByteArray.toRequestBody(mediaType, 0, pieceByteArray.size)
            val body            = RequestBody.create(MediaType.parse("application/octet-stream"), pieceByteArray)
            val basicAuthName   = "web"
            val basicAuthPass   = "web"
            val token           = "$basicAuthName:$basicAuthPass".toByteArray()
            val request         = Request.Builder()
                                .header("Authorization", "Basic " + Base64.encode(token))
                                .url("https://seawolf.auxi.ru/photolier/hs/App/Order/New")
                    //            .url("https://seawolf.auxi.ru/photolier/hs/App/OrderNew")
                                .post(body)
                    //            .addHeader("Content-Type", "image/png")
                    //            .addHeader("Content-Type", "application/octet-stream")
                                .header("id", jsonObjHead.toString())
                                .header("num", jsonObject.toString())

                                .build()

//             var protocols = arrayListOf(Protocol.HTTP_2)

//            var clientH2 = client.newBuilder().protocols(protocols).build()

            val response        = client.newCall(request).execute()
//            val response        = clientH2.newCall(request).execute()
            val res             = response.body()!!.string()

            response.body()?.close()

            when (val responseCode = response.code()){
                200 ->{}
                else -> {

                    mainAct.saveLog("responseCode = $responseCode")
                    mainAct.saveLog(res)

                }
            }

        } catch ( e : IOException) {
            val res = "IOException $e"
            mainAct.saveLog(res)
        } catch ( e : JSONException) {
            val res = "JSONException $e"
            mainAct.saveLog(res)
        }


//        jsonObject.remove("pieceOfData")
//        jsonObject.remove("thumbB64String")


//        mainAct.saveLog(jsonObject.toString())
//        mainAct.saveLog(res)

//        var res = ""
//
//        try {
//
//            val request = SoapObject(NAMESPACE, SEND_ORDER_METHOD_NAME)
//
//            request.addProperty("ID"    , jsonString)
//            request.addProperty("Num"   , jsonObject.toString())
//
////-----------------------------------------------------------------------------
//            res = sendSoapObject(request, SEND_ORDER_ACTION)
//
//        } catch (e: Exception) {
//
//            e.printStackTrace()
//
//            res = e.toString()
//
//            mainAct.saveLog("Ошибка!---$res")
//
////            saveLog("sending $index img")
//        }

        return res
    }



    private fun sendSoapObject(soapObject: SoapObject, action: String): String {

        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER12)
        val timeOut = 120000//60000

        envelope.setOutputSoapObject(soapObject)

        val androidHttpTransport        = HttpTransportSE(URL, timeOut)
            androidHttpTransport.debug  = true


        var res = ""

//        try {

            val headerList      : MutableList<HeaderProperty>   = ArrayList()
            val basicAuthName   : String                        = "web"
            val basicAuthPass   : String                        = "web"

            if (basicAuthName != null && basicAuthPass != null) {

                val token = "$basicAuthName:$basicAuthPass".toByteArray()

                headerList.add(HeaderProperty("Authorization", "Basic " + Base64.encode(token)))
            }

//            headerList.add(HeaderProperty("Connection", "Close"))
//            headerList.add(HeaderProperty("Connection", "keep-alive"))
//            headerList.add(HeaderProperty("Keep-Alive", "100"))

            try {

                //----------------------------------------------
                androidHttpTransport.call(action, envelope, headerList)
                //----------------------------------------------

                res = (envelope.bodyIn as SoapObject).getPropertyAsString(0)

                androidHttpTransport.reset()
                androidHttpTransport.serviceConnection.disconnect()

            } catch (e: Exception) {

                androidHttpTransport.reset()
                androidHttpTransport.serviceConnection.disconnect()

                e.printStackTrace()

                res = e.toString()


                when(envelope.bodyIn){
                    null->{

                    }
                    else->{
                        res = (envelope.bodyIn as SoapFault).faultstring + "\n" + e.toString()
                    }
                }

                when (action){
                    GET_FORMATS_SOAP_ACTION ->{
                        ImageFormat.status = ImageFormat.SYNCERR
                        ImageFormat.syncerr = res
                    }
                    GET_MATERIALS_SOAP_ACTION ->{

                        MaterialPhoto.status = MaterialPhoto.SYNCERR
                        MaterialPhoto.syncerr = res
                    }
                    else ->{

                    }
                }
            }

//            return res
//
//        } catch (e: Exception) {
//
//            e.printStackTrace()
//
//            res = e.toString()
//        }
//
//        androidHttpTransport.reset()
//        androidHttpTransport.serviceConnection.disconnect()

        return res
    }

    fun getOrders(jsonString: String , displayName : String): String {

        val request = SoapObject(NAMESPACE, GET_ORDERS_METHOD_NAME)
        var res     = ""

        request.addProperty("userUUid", jsonString)
        request.addProperty("displayName", displayName)

        res = sendSoapObject(request, GET_ORDERS_SOAP_ACTION)

        return res
    }

    fun getOrder(orderUuid : String): String {

        val request = SoapObject(NAMESPACE, GET_ORDER_METHOD_NAME)
        var res     = ""

        request.addProperty("orderUuid", orderUuid)

        res = sendSoapObject(request, GET_ORDER_SOAP_ACTION)

        return res
    }

    fun getFormatsByMaterial(materialUid : String) : String{

        val request = SoapObject(NAMESPACE, GET_FORMATSBYMATERIAL_METHOD_NAME)
        var res = ""

        request.addProperty("materialUid", materialUid )

        res = sendSoapObject(request, GET_FORMATSBYMATERIAL_SOAP_ACTION)

        return res

    }

    companion object {
        private const val NAMESPACE = "http://www.w3.org/2001/XMLSchema"

        // private const val URL = "https://seawolf.auxi.ru/photolier/ws/App/wsApp.1cws?wsdl"
        private const val URL = "https://seawolf.auxi.ru/photolier/ws/App/wsApp.1cws"

        private const val SEND_ORDER_ACTION       = "http://www.w3.org/2001/XMLSchema#App:ID"
        private const val SEND_ORDER_METHOD_NAME = "ID"

        private const val GET_ORDERS_SOAP_ACTION = "http://www.w3.org/2001/XMLSchema#App:getOrders"
        private const val GET_ORDERS_METHOD_NAME = "getOrders"

        private const val GET_ORDER_SOAP_ACTION = "http://www.w3.org/2001/XMLSchema#App:getOrder"
        private const val GET_ORDER_METHOD_NAME = "getOrder"

        private const val GET_FORMATS_SOAP_ACTION = "http://www.w3.org/2001/XMLSchema#App:getFormaats"
        private const val GET_FORMATS_METHOD_NAME = "getFormaats"

        private const val GET_MATERIALS_SOAP_ACTION = "http://www.w3.org/2001/XMLSchema#App:getFormaats"
        private const val GET_MATERIALS_METHOD_NAME = "getFormaats"

        private const val GET_FORMATSBYMATERIAL_SOAP_ACTION = "http://www.w3.org/2001/XMLSchema#App:getFormatsByMaterial"
        private const val GET_FORMATSBYMATERIAL_METHOD_NAME = "getFormatsByMaterial"

        @JvmStatic
        fun fillFormats(resArray: JSONArray) {

            /*
            "Код": "000000018",
            "Наименование": "09 х 13",
            "ПометкаУдаления": false,
            "Высота": 9,
            "Ширина": 13,
            "uid": "7a3e31ea-77b7-11eb-b993-60a44c65164b"
     */
            ImageFormat.imageFormats = ArrayList()

            for (i: Int in 0 until resArray.length()) {

                val item        = JSONObject(resArray[i].toString())//resArray.getJSONObject(i)
                val height      = item.getInt("height")
                val width       = item.getInt("width")
                val heightPix   = item.getInt("heightPix")
                val widthPix    = item.getInt("widthPix")
                val hash        = item.getInt("hash")
                val uid         = item.getString("uid")
                val name        = item.getString("name")

                val imageFormat = ImageFormat(width, height, uid, name, hash, widthPix , heightPix)

                ImageFormat.imageFormats.add(imageFormat)
            }
        }

        @JvmStatic
        fun fillMaterials(resArray: JSONArray) {

            MaterialPhoto.materialsPhoto = ArrayList()

            for (i: Int in 0 until resArray.length()) {

                val item            = JSONObject(resArray[i].toString())//resArray.getJSONObject(i)
                val uid             = item.getString("uid")
                val name            = item.getString("name")
                val hash            = item.getInt("hash")
                val materialPhoto   = MaterialPhoto(uid, name, hash)

                MaterialPhoto.materialsPhoto.add(materialPhoto)

            }
        }

        @JvmStatic
        fun sync(context: Context, sourceName: String): JSONObject {

            (context as MainActivity).saveLog("sync $sourceName")

            val hashArrayList: MutableList<Int> = ArrayList()

            hashArrayList.add(1)
            hashArrayList.add(2)
            hashArrayList.add(3)

            val progressBar                     = (context as MainActivity).findViewById<ProgressBar>(R.id.progressBar)
            progressBar.visibility              = View.VISIBLE //rogressBar.VISIBLE
            val dl                              = DataLoader()
            val sendResult                      = dl.syncFormats(hashArrayList, context, sourceName)
            var resultJSSONObj                  = JSONObject()
            var succ: Boolean                   = (sendResult != "")
            progressBar.visibility              = View.INVISIBLE

            //(context as MainActivity).saveLog("result $sourceName : $sendResult")

            val c   : Collection<String>

            c = ArrayList()

            c.add("java.io.IOException: unexpected end of stream on com.android.okhttp.Address")
            c.add("java.lang.NullPointerException: null cannot be cast to non-null type org.ksoap2.SoapFault")
            c.add("java.lang.NullPointerException")

            when (sendResult.findAnyOf(c,ignoreCase = true)?.first){
                0 -> {
                    succ = false
                }
            }

            when (succ) {
                true -> {

                    try {
                        resultJSSONObj = JSONObject(sendResult)
                    }
                    catch (e: Exception) {

                        e.printStackTrace()

                        res         = sendResult + "\n" + e.toString()
                        val jObject = JSONObject()

                        jObject.put("res", res)
                        //(context as MainActivity).log.add(res)
                        (context as MainActivity).saveLog(res)

                        return jObject
                    }

                    when (resultJSSONObj.getBoolean("succ")) {
                        true -> {

                            val resArray = resultJSSONObj.getJSONArray("resArray")

                            when (sourceName) {
                                "Справочник.Форматы" -> {

                                    fillFormats(resArray)

                                }
                                "Справочник.Материалы" -> {

                                    fillMaterials(resArray)
                                }
                                else -> {

                                }
                            }
                        }
                        false->{
                            //(context as MainActivity).log.add(sendResult)
                            (context as MainActivity).saveLog(sendResult)
                        }
                    }
                }
                false->{
                    //(context as MainActivity).log.add(sendResult)
                    (context as MainActivity).saveLog(sendResult)

                }
            }
            return resultJSSONObj
        }

        @JvmStatic
        var res = ""
        @JvmStatic
        var syncSucc = false
    }
}