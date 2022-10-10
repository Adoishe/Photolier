package com.adoishe.photolier

import android.app.Activity
import android.net.Uri
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class Order(var context: Activity) {
    private     var uuid            : String                    = "" //UUID.randomUUID().toString()
                var session         : String                    = ""
                var name            : String                    = ""
                var text            : String                    = ""
                var imageFormat     : ImageFormat?              = null
               // var materialPhoto   : MaterialPhoto?            = null
                var materialPhoto   : Any?            = null
                var imageUriList    : MutableList<Uri>          = ArrayList()
                var imageBase64List : MutableList<String>       = ArrayList()
    //private     var byteArrayList   : MutableList<ByteArray>    = ArrayList()
                var imageOrderList  : MutableList<ImageOrder>   = ArrayList()
                var result                                      = String()
                var orderStatus     : String                    = ""
//                var orderSendResult : String                    = ""
                var indexInPacket   : Int                       = 0
                var countOfPacket   : Int                       = 0
                var status          : Int                       = NEW
                var payed           : Boolean                   = false
    private     val mainAct                                     = context as MainActivity
    lateinit    var userId          : String                    //= mainAct.auth.currentUser?.uid.toString()
    lateinit    var orderSendResult : String
    private     val job                                         = SupervisorJob()
    private     val scope                                       = CoroutineScope(Dispatchers.Default + job)
//    private     val scope                                       = CoroutineScope(Dispatchers.Main + job)

    init {
        this.name       = "blanc"//(context as MainActivity ).resources.getString(R.string.netTrouble)
        this.session    = mainAct.session
        this.uuid       = UUID.randomUUID().toString()

        when(mainAct.auth.currentUser){
            null -> Toast.makeText(mainAct, "NO AUTH!!!!!", Toast.LENGTH_LONG).show()
            else -> this.userId     = mainAct.auth.currentUser?.uid.toString()
        }
    }

    override fun toString() = name + "\n" + orderStatus

    fun getUuid(): String {

        return uuid

    }

    fun setUuid(uuid: String) {

        this.uuid = uuid
    }
/*
    fun fillImagesThumbsByBase64List(imageBase64List : MutableList<String>){

        this.imageBase64List = imageBase64List

        imageBase64List.forEach{

            val imageOrder = ImageOrder()

            imageOrder.setThumb(it)

            imageOrderList.add(imageOrder)
        }

    }

    private fun getByteArrayList() : MutableList<ByteArray>{

        imageOrderList.forEach(){

            val byteArray = context.contentResolver.openInputStream(it.imageUri!!)!!.readBytes()

            byteArrayList.add(byteArray)
        }

        return byteArrayList
    }

    fun getCvArrayList() : MutableList<ContentValues>{

        val cvArrayList    : MutableList<ContentValues>  = ArrayList()

        imageOrderList.forEach(){

            val cv          = ContentValues()
            val byteArray   = context.contentResolver.openInputStream(it.imageUri!!)!!.readBytes()

            cv.put("name", it.name)
            cv.put("byteArray", Base64.encode(byteArray))

            cvArrayList.add(cv)

        }

        return cvArrayList
    }

 */

    private fun getJSONArrayListSingle(imageOrder : ImageOrder) : JSONArray{

        val jsonArrayList   =  JSONArray()
//        var byteArray       = context.contentResolver.openInputStream(imageOrder.imageUri!!)!!.readBytes()

//        // тут размер картинки приводится к номинальному для перчати. Но похоже оно жрет память. Отключаю
//        var bitMap          = BitmapFactory.decodeByteArray(byteArray , 0, byteArray.size)
//        var bos             = ByteArrayOutputStream()
//// тут размер картинки приводится к номинальному для перчати. Но похоже оно жрет память. Отключаю


//        bitMap.compress(CompressFormat.JPEG, 100, bos)
//
//        val base64String                = Base64.encode(bos.toByteArray())
//        // Разбиваем строку на список строк с указанным числом символов. В последней строке может выводиться остаток
//        val partSize                    = 1023
//        val base64Sliced:List<String>   = base64String.chunked(partSize)

 // тут размер картинки приводится к номинальному для перчати. Но похоже оно жрет память. Отключаю
//        val maxSize                     = maxOf( imageOrder.imageFormat!!.heightPix , imageOrder.imageFormat!!.widthPix)
//
//        when (maxSize >0 )
//        {
//            true -> bitMap = mainAct.resizeBitmap(bitMap, maxSize )
//            false -> {}
//            else -> {}
//        }
//
//        bitMap.compress(CompressFormat.JPEG, 100, bos)
//
//
////        bitMap = null
//
//        byteArray = bos.toByteArray()
//
//        bos.flush()
//        bos.close()
//
//       bitMap.recycle()
//-------------------------------------------------------------------------------------------------------------

//        bos = null

//        val base64String                = Base64.encode(byteArray)
//        val base64String                =  encodeToString(byteArray, Base64.NO_WRAP)
        val partSize                    = 65536//32768//16384//8192//4096 //8192
        var byteArraySliced             = context
                                        .contentResolver
                                        .openInputStream(imageOrder.imageUri!!)!!
                                        .readBytes()
                                        .toList()
                                        .chunked(partSize)

//        byteArray = ByteArray(0)

//        byteArraySliced[0].toByteArray()

        // Разбиваем строку на список строк с указанным числом символов. В последней строке может выводиться остаток

//        val base64Sliced:List<String>   = base64String.chunked(partSize)


//        var base64Splitted = splitByCount(20, base64String)

//        base64Sliced.forEachIndexed{ index, pieceOfB64string ->
//        base64Splitted.forEachIndexed{ index, pieceOfB64string ->
        byteArraySliced.forEachIndexed{ index, pieceOfData ->

            val jsonObj = JSONObject()

            jsonObj.put("name"           , imageOrder.name)
            jsonObj.put("imageUuid"      , imageOrder.uuid)
            jsonObj.put("materialPhoto"  , imageOrder.materialPhoto!!.uid)
            jsonObj.put("imageFormat"    , imageOrder.imageFormat!!.uid)
            jsonObj.put("price"          , imageOrder.imageFormat!!.price.toString())
            jsonObj.put("qty"            , imageOrder.qty)
            jsonObj.put("base64String"   , "")//pieceOfB64string)
//            jsonObj.put("base64Size"     , base64Sliced.size)
            jsonObj.put("base64Size"     , byteArraySliced.size)
            jsonObj.put("base64Index"    , index)
            jsonObj.put("pieceOfData"    , pieceOfData.toByteArray())
//            jsonObj.put("base64Sliced"   , pieceOfB64string)
            jsonObj.put("thumbB64String" , "")//imageOrder.imageThumbBase64)
            jsonObj.put("imageUri"       , imageOrder.imageUri)
            jsonObj.put("lastOne"        , imageOrder.lastOne)
            jsonObj.put("size"           , imageOrderList.size)

            val result = JSONObject()

            result.put("mValues", jsonObj)

            jsonArrayList.put(result)
        }

        byteArraySliced = listOf()

        return jsonArrayList
    }

    private fun getJSONForWs() : JSONObject{

        val json    = JSONObject()
        val result  = JSONObject()

        json.put("orderUid"         , uuid)
        json.put("session"          , session)
        json.put("displayName"      , mainAct.auth.currentUser?.displayName.toString())
        json.put("email"            , mainAct.auth.currentUser?.email.toString())
        json.put("phoneNumber"      , mainAct.auth.currentUser?.phoneNumber.toString())
        json.put("uid"              , Profile.profile.guid)//mainAct.auth.currentUser?.uid.toString())
        json.put("indexInPacket"    , this.indexInPacket)
        json.put("countOfPacket"    , this.countOfPacket)
        json.put("tokenPush"        , Profile.profile.pushToken)

        result.put("mValues", json)

        return result
    }

    private fun sendImageOrderPieceAsync(
//                                                pieceIndex  : Int
//                                            ,   piecesCount : Int
//                                            ,   jSONArray   : JSONArray
//                                            ,
                                                jsonObjHead  : JSONObject
//                                            ,   fragment    : OrderFragment
//                                            ,   imageOrder  : ImageOrder
//                                            ,   thisIsLastIO : Boolean
                                            ,   jsonObj : JSONObject
                                        ) : Deferred<Unit> = scope.async {

        val dl                  = DataLoader()
        val sendResult          = dl.sendOrderOverHTTP(jsonObjHead, jsonObj, context as MainActivity)

    }


    private fun sendImageOrderAsync(imageOrder: ImageOrder, index : Int , fragment: OrderFragment): Unit{

        var sendResult              = ""
        val dl                      = DataLoader()
        val jsonObjHead             = getJSONForWs()//jsonObject
        val imagesCount             = imageOrderList.size -1
        val thisIsLastOne           = (imagesCount ==  index)
        val jSONArray               = getJSONArrayListSingle(imageOrder)
        val piecesCount             = jSONArray.length();

            imageOrder.piecesCount  = piecesCount

        val uiInfo  = Runnable {

            fragment.textViewResult.text    = String.format(mainAct.resources.getString(R.string.sending), index + 1, imageOrderList.size);

        val progressBar                     = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal)
            progressBar.id                  = index
            progressBar.visibility          = ProgressBar.VISIBLE
            progressBar.isIndeterminate     = false
            progressBar.max                 = imageOrder.piecesCount
            progressBar.min                 = 0
            progressBar.layoutParams        = LinearLayout.LayoutParams(MATCH_PARENT, 50)//,WRAP_CONTENT)

        val orderRootLay                    = fragment.requireView().findViewById<LinearLayout>(R.id.orderRoot)

            orderRootLay.addView(progressBar)

        }

        mainAct.runOnUiThread(uiInfo)

        for (pieceIndex in 0 until piecesCount) {

            mainAct.runOnUiThread{

                val progressByImage = 100000 / imageOrderList.size
                val progressByPiece = (progressByImage / piecesCount).toInt()

                fragment.progressBar.incrementProgressBy(progressByPiece)
            }

            val jsonObj = jSONArray.getJSONObject(pieceIndex)

//            jSONArray.put(pieceIndex , JSONObject())

            sendImageOrderPieceAsync(
//                                            pieceIndex
//                                        ,   piecesCount
//                                        ,   jSONArray
//                                        ,
                                            jsonObjHead
//                                        ,   fragment
//                                        ,   imageOrder
//                                        ,   thisIsLastOne
                                        ,   jsonObj
                                    ).start()
        }
    }

    private fun sendImageOrder(imageOrder: ImageOrder, index : Int , fragment: OrderFragment): String {

        var sendResult              = ""
        val jsonObject              = getJSONForWs()
        val dl                      = DataLoader()
        val outputJson              = jsonObject.toString()
        val lastIndex               = imageOrderList.size - 1
        val thisIsLastOne           = (lastIndex ==  index)

        when (thisIsLastOne) {

            true    -> mainAct.saveLog("lastOne sending $index img")
            false   -> mainAct.saveLog("sending $index img")
        }

        imageOrder.isLastOne(thisIsLastOne)

        val uiInfo  = Runnable {

            fragment.textViewResult.text    = String.format(mainAct.resources.getString(R.string.sending), index + 1, imageOrderList.size);
            fragment.progressBar.progress   = index

        }

        mainAct.runOnUiThread(uiInfo)

        val jSONArray   = getJSONArrayListSingle(imageOrder)
        val piecesCount = jSONArray.length()

        mainAct.runOnUiThread{
            fragment.progressBarPiece.visibility         = View.VISIBLE
            fragment.progressBarPiece.isIndeterminate    = false
            fragment.progressBarPiece.max                = piecesCount-1
            fragment.progressBarPiece.min                = 0
        }

        for (pieceIndex in 0 until piecesCount) {

            mainAct.saveLog("send $pieceIndex of $piecesCount")

            val jsonObj     = jSONArray.getJSONObject(pieceIndex)
                sendResult  = dl.sendOrder(outputJson, jsonObj, context as MainActivity)

                mainAct.saveLog(sendResult)

            val ordersUiInfoPiece  = mainAct.newFirebaseUiPiece ("orders", fragment, imageOrder)

            mainAct.runOnUiThread(ordersUiInfoPiece)
        }

        if (thisIsLastOne)  workWithResult(sendResult, fragment)

        return sendResult
    }

     fun workWithResult(sendResult:String, fragment: OrderFragment){

         try {

             val resultJSSONObj = JSONObject(sendResult)
             result             = resultJSSONObj.toString()
             name               = resultJSSONObj.getString("orderName")
             orderStatus        = resultJSSONObj.getString("orderStatus")
             orderSendResult    = name
             status             = SENT

             mainAct.runOnUiThread {

                 fragment.arguments?.putString("orderName", name)
                 fragment.arguments?.putString("orderStatus", orderStatus)
                 fragment.arguments?.putString("orderUuid", resultJSSONObj.getString("orderUuid"))

                 fragment.fillBySend(fragment.requireView().rootView)

             }
             mainAct.sendNotification(null, resultJSSONObj)
         }
        catch (e: Exception) {
            // тууут ошибка загрузки заказа
            result = sendResult
            status = SEND_ERROR

        }
         mainAct.saveLog("result = $result")
    }

    fun workWithImageOrderLog(snapshot: DataSnapshot, fragment: OrderFragment){

        val imageOrderFound = imageOrderList.find { imageOrder -> imageOrder.uuid == snapshot.key}

        when (imageOrderFound!!.piecesCount == snapshot.childrenCount.toInt()){

            true -> imageOrderFound.sent = true

            false -> {

                val indexImageOrder = imageOrderList.indices.find { imageOrderList[it].uuid == imageOrderFound.uuid }
                val progressBar     = fragment.requireView().findViewById<ProgressBar>(indexImageOrder!!)

                progressBar.incrementProgressBy(1)
            }
        }
    }
    private fun sendImageOrderByCoroutinesAsync(imageOrder: ImageOrder, index : Int , fragment: OrderFragment): Deferred<Unit> = scope.async {

        sendImageOrderAsync(imageOrder, index , fragment)

    }

    private fun getNewChildListener(fragment: OrderFragment, ref : DatabaseReference) :ChildEventListener{

        return object :ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                when (snapshot.key) {
                    "sendResult" -> workWithResult(snapshot.value.toString() , fragment)
                    // обработка результата получения части файла
                    else ->  workWithImageOrderLog(snapshot, fragment)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                when (snapshot.key) {
                    "sendResult" -> {

                        ref.removeEventListener(this)

                        workWithResult(snapshot.value.toString() , fragment)

                    }
                    else-> {
                        workWithImageOrderLog(snapshot, fragment)
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun sendByCoroutines(fragment: OrderFragment) {

        mainAct.runOnUiThread{
            fragment.progressBar.visibility         = View.VISIBLE
            fragment.progressBar.isIndeterminate    = false
            fragment.progressBar.max                = 100000//imageOrderList.size-1
            fragment.progressBar.min                = 0
        }

        val ref         =   FirebaseDatabase
                            .getInstance(MainActivity.FIREINSTANCE)
                            .getReference("orders")
                            .child(session)

        val listener    = getNewChildListener(fragment , ref)

        ref.addChildEventListener(listener)

        imageOrderList.forEachIndexed  { index, imageOrder ->

            scope.launch() {

                val deferred = sendImageOrderByCoroutinesAsync(imageOrder, index , fragment)
//                deferred.await()
                deferred.start()
            }
        }
    }

    private fun getSendThread(fragment: OrderFragment) : Thread {
        return Thread {

            mainAct.saveLog("send thread create")

            var sendResult = ""

            mainAct.runOnUiThread{
                fragment.progressBar.visibility         = View.VISIBLE
                fragment.progressBar.isIndeterminate    = false
                fragment.progressBar.max                = imageOrderList.size-1
                fragment.progressBar.min                = 0
            }

            imageOrderList.forEachIndexed  { index, imageOrder ->
                sendResult = sendImageOrder(imageOrder, index , fragment)
            }
        }
    }

    fun send (fragment: OrderFragment){

        when (imageOrderList.size){
            0 -> {
                mainAct.saveLog("nothing to send ")
            }
            else ->{

                mainAct.setWakeLock()

                sendByCoroutines(fragment)

            }//else ->{
        }//when (imageOrderList.size)
    }//fun send (

    companion object {

        @JvmStatic
        fun sendAll(fragment: OrderFragment){

            updateIndices()

            ordersArray.forEach {

                it.send(fragment)

            }
        }
        @JvmStatic
        fun updateIndices(){

            ordersArray.forEach { currentOrder ->

                currentOrder.indexInPacket = ordersArray.indexOf(currentOrder) + 1
                currentOrder.countOfPacket = ordersArray.size
            }
        }

        @JvmStatic
                var ordersArray : MutableList<Order>    = ArrayList()
        const   val NEW         : Int                   = 0 // 0 - new
        const   val SENT        : Int                   = 1 // 1 - sent
        const   val SEND_ERROR  : Int                   = 3 // 3 - send error
    }
}