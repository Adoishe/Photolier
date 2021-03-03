package com.adoishe.photolier

import android.content.ContentValues
import com.google.gson.Gson
import org.kobjects.base64.Base64
import org.ksoap2.HeaderProperty
import org.ksoap2.SoapEnvelope
import org.ksoap2.SoapFault
import org.ksoap2.serialization.MarshalBase64
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE



class DataLoader () {

    private             var byteArrayList    : MutableList<ByteArray>  = ArrayList()
    private             var stringArrayList    : MutableList<String>  = ArrayList()

    fun sendOrder(jsonString: String , jsoncvArrayList :String): String {

        var res = ""

            try {

                val request     = SoapObject(NAMESPACE, SEND_ORDER_METHOD_NAME)

                request.addProperty("ID", jsonString)
                request.addProperty("Num", jsoncvArrayList )
//-----------------------------------------------------------------------------
                res = sendSoapObject(request, SEND_ORDER_ACTION)

            } catch (e: Exception) {

                e.printStackTrace()

                res =  e.toString()
            }
        return res
    }

    private fun sendSoapObject(soapObject : SoapObject, action : String) : String{

        val envelope    = SoapSerializationEnvelope(SoapEnvelope.VER12)

        envelope.setOutputSoapObject(soapObject)

        val androidHttpTransport    = HttpTransportSE(URL)
        androidHttpTransport.debug  = true
        var res                     = ""

        try {

            val headerList      : MutableList<HeaderProperty>   = ArrayList()
            val basicAuthName   : String                        = "web"
            val basicAuthPass   : String                        = "web"

            if (basicAuthName != null && basicAuthPass != null) {

                val token = "$basicAuthName:$basicAuthPass".toByteArray()

                headerList.add(HeaderProperty("Authorization", "Basic " + Base64.encode( token )))
            }

            headerList.add(HeaderProperty("Connection", "Close"))
            androidHttpTransport.call(action, envelope, headerList)

            try {

                val resultsRequestSOAP  = envelope.bodyIn as SoapObject
                res                    = resultsRequestSOAP.getPropertyAsString(0)

            }
            catch (e: Exception) {

                res =  (envelope.bodyIn as SoapFault).faultstring
            }

            return  res

        } catch (e: Exception) {

            e.printStackTrace()

            res =  e.toString()
        }

        return res
    }

    fun getOrders(jsonString: String ) : String {

        val request = SoapObject(NAMESPACE, GET_ORDERS_METHOD_NAME)
        var res     = ""

        request.addProperty("userUUid", jsonString)

        res = sendSoapObject(request, GET_ORDERS_SOAP_ACTION)

        return  res
    }

    companion object {
        private const val NAMESPACE = "http://www.w3.org/2001/XMLSchema"
       // private const val URL = "https://seawolf.auxi.ru/photolier/ws/App/wsApp.1cws?wsdl"
        private const val URL = "https://seawolf.auxi.ru/photolier/ws/App/wsApp.1cws"
            //private const val SOAP_ACTION = "https://seawolf.auxi.ru/photolier/ws/App/wsApp"
        private const val SEND_ORDER_ACTION = "http://www.w3.org/2001/XMLSchema#App:ID"
        private const val GET_ORDERS_SOAP_ACTION = "http://www.w3.org/2001/XMLSchema#App:getOrders"
        private const val SEND_ORDER_METHOD_NAME = "ID"
        private const val GET_ORDERS_METHOD_NAME = "getOrders"
    }
}