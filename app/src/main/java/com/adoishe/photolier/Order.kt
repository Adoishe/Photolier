package com.adoishe.photolier

//import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread


//import org.kobjects.base64.Base64
import android.app.Activity
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64.encodeToString
import android.view.View
import android.widget.Toast
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import android.util.Base64
import com.google.firebase.database.*
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList


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
    lateinit    var orderSendResult          : String
    private     val job                                         = SupervisorJob()
    private     val scope                                       = CoroutineScope(Dispatchers.Default + job)

    init {
        this.name       = "blanc"//(context as MainActivity ).resources.getString(R.string.netTrouble)
        this.session    = mainAct.session
        this.uuid       = UUID.randomUUID().toString()

//        mainAct.authenticate()

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

    private fun splitByCount(count : Int , source :String) : List<String>{

        var result: MutableList<String> = mutableListOf()

        var stepsQty = (source.length / count).toInt()

        var index = 0

        for (indexStep  in 0 until count){

            try{

                var subResult = source.slice(index until (index + stepsQty))

                result.add(subResult)

            }catch (e:Exception){

                var subResult = source.slice(index until source.length)

                result.add(subResult)
            }

            index += stepsQty
        }

        return result.toList()
    }

    private fun getJSONArrayListSingle(imageOrder : ImageOrder) : JSONArray{

        val jsonArrayList   =  JSONArray()
        var byteArray       = context.contentResolver.openInputStream(imageOrder.imageUri!!)!!.readBytes()
        var bitMap          = BitmapFactory.decodeByteArray(byteArray , 0, byteArray.size)
        val bos             = ByteArrayOutputStream()

//        bitMap.compress(CompressFormat.JPEG, 100, bos)
//
//        val base64String                = Base64.encode(bos.toByteArray())
//        // Разбиваем строку на список строк с указанным числом символов. В последней строке может выводиться остаток
//        val partSize                    = 1023
//        val base64Sliced:List<String>   = base64String.chunked(partSize)
        val maxSize                     = maxOf( imageOrder.imageFormat!!.heightPix , imageOrder.imageFormat!!.widthPix)

        when (maxSize >0 )
        {
            true -> bitMap = mainAct.resizeBitmap(bitMap, maxSize )
            false -> {}
            else -> {}
        }

        bitMap.compress(CompressFormat.JPEG, 100, bos)
        byteArray                       = bos.toByteArray()
//        val base64String                = Base64.encode(byteArray)
        val base64String                =  encodeToString(byteArray, Base64.NO_WRAP)



        // Разбиваем строку на список строк с указанным числом символов. В последней строке может выводиться остаток
        val partSize                    = 32768//16384//8192//4096 //8192
        val base64Sliced:List<String>   = base64String.chunked(partSize)


//        var base64Splitted = splitByCount(20, base64String)

        base64Sliced.forEachIndexed{ index, pieceOfB64string ->
//        base64Splitted.forEachIndexed{ index, pieceOfB64string ->

            val jsonObj = JSONObject()

            jsonObj.put("name"           , imageOrder.name)
            jsonObj.put("imageUuid"      , imageOrder.uuid)
            jsonObj.put("materialPhoto"  , imageOrder.materialPhoto!!.uid)
            jsonObj.put("imageFormat"    , imageOrder.imageFormat!!.uid)
            jsonObj.put("price"          , imageOrder.imageFormat!!.price.toString())
            jsonObj.put("qty"            , imageOrder.qty)
            jsonObj.put("base64String"   , pieceOfB64string)
            jsonObj.put("base64Size"     , base64Sliced.size)
            jsonObj.put("base64Index"    , index)
//            jsonObj.put("base64Sliced"   , pieceOfB64string)
            jsonObj.put("thumbB64String" , imageOrder.imageThumbBase64)
            jsonObj.put("imageUri"       , imageOrder.imageUri)
            jsonObj.put("lastOne"        , imageOrder.lastOne)
            jsonObj.put("size"           , imageOrderList.size)

            val result = JSONObject()

            result.put("mValues", jsonObj)

            jsonArrayList.put(result)
        }

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

        result.put("mValues", json)

        return result
    }

    private fun sendImageOrderPieceAsync(       pieceIndex  : Int
                                            ,   piecesCount : Int
                                            ,   jSONArray   : JSONArray
                                            ,   outputJson  : String
                                            ,   fragment    : OrderFragment
                                            ,   imageOrder  : ImageOrder
                                            ,   thisIsLastIO : Boolean
                                        ) : Deferred<Unit> = scope.async {

        //mainAct.saveLog("send $pieceIndex   of $piecesCount")
        val dl                  = DataLoader()
        val thisIsLastPiece     = pieceIndex == piecesCount -1
        val jsonObj             = jSONArray.getJSONObject(pieceIndex)
        val sendResult          = dl.sendOrder(outputJson, jsonObj)

//        mainAct.saveLog("sendResult $sendResult")


    }

//    private fun workWithResultAcync() : Deferred<Unit> = scope.async {
//
//
//        val ref                 = FirebaseDatabase.getInstance(MainActivity.FIREINSTANCE).getReference("orders")
//        val valueEventListener  = object : ValueEventListener {
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//
//                when (snapshot.value){
//                    true-> mainAct.saveLog("заказ загружен")
//                }
//
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        }
//
//        ref.child(session).child("receivedOrder").addListenerForSingleValueEvent(valueEventListener)
//
//
//    }

    private fun sendImageOrderAsync(imageOrder: ImageOrder, index : Int , fragment: OrderFragment): Unit{

        var sendResult              = ""
        val jsonObject              = getJSONForWs()
        val dl                      = DataLoader()
        val outputJson              = jsonObject.toString()
        val lastIndex               = imageOrderList.size -1
        val thisIsLastOne           = (lastIndex ==  index)

//        when (thisIsLastOne) {
//
//            true    -> mainAct.saveLog("lastOne sending $index img")
//            false   -> mainAct.saveLog("sending $index img")
//        }

        mainAct.saveLog("sending $index img")

//        imageOrder.isLastOne(thisIsLastOne)



        val jSONArray   = getJSONArrayListSingle(imageOrder)
        val piecesCount = jSONArray.length();

//        mainAct.runOnUiThread{
//            fragment.progressBarPiece.visibility         = View.VISIBLE
//            fragment.progressBarPiece.isIndeterminate    = false
//            fragment.progressBarPiece.max                = piecesCount-1
//            fragment.progressBarPiece.min                = 0
//        }

//        val uiInfoPiece  = Runnable {
//
//            val ref                 = FirebaseDatabase.getInstance(MainActivity.FIREINSTANCE).getReference("orders")
//            val valueEventListener  = object : ValueEventListener {
//
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    fragment.progressBarPiece.progress   = snapshot.childrenCount.toInt()
//
//
//
//                    mainAct.saveLog(snapshot.childrenCount.toString())
//
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
//                }
//            }
//
//            ref.child(session).child(imageOrder.uuid).addListenerForSingleValueEvent(valueEventListener)
//
//        }
//
//        mainAct.runOnUiThread(uiInfoPiece)


        for (pieceIndex in 0 until piecesCount) {

//            scope.launch {
                sendImageOrderPieceAsync(
                                    pieceIndex
                                ,   piecesCount
                                ,   jSONArray
                                ,   outputJson
                                ,   fragment
                                ,   imageOrder
                                ,   thisIsLastOne
                            ).start()

//                mainAct.saveLog("sendResult $sendResult")
//            }



//            mainAct.saveLog("send $pieceIndex   of $piecesCount")
//
//            val jsonObj     = jSONArray.getJSONObject(pieceIndex)
//                sendResult  = dl.sendOrder(outputJson, jsonObj)
//
//            val uiInfoPiece  = Runnable {
//
//                val ref = FirebaseDatabase.getInstance(MainActivity.FIREINSTANCE).getReference("orders")
//
//                val valueEventListener = object : ValueEventListener {
//
//                    override fun onDataChange(snapshot: DataSnapshot) {
//
//                        fragment.progressBarPiece.progress   = snapshot.childrenCount.toInt()
//
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        TODO("Not yet implemented")
//                    }
//
//                }
//
//                ref.child(session).child(imageOrder.uuid).addListenerForSingleValueEvent(valueEventListener)
//
////                        fragment.progressBarPiece.progress   = pieceIndex
//
//            }
//
//            mainAct.runOnUiThread(uiInfoPiece)

        }

//        if (thisIsLastOne)  workWithResult(sendResult, fragment)

//        return sendResult


    }

    private fun sendImageOrder(imageOrder: ImageOrder, index : Int , fragment: OrderFragment): String {

        var sendResult              = ""
        val jsonObject              = getJSONForWs()
        val dl                      = DataLoader()
        val outputJson              = jsonObject.toString()
        val lastIndex               = imageOrderList.size -1
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

        //uiInfo.wait() ; // unlocks myRunable while waiting
        //  }

        // mainAct.runOnUiThread(uiInfo )

/*

                val photosFragment = mainAct.supportFragmentManager.fragments[0].childFragmentManager.fragments[0] as PhotosFragment

                photosFragment.imageUriList.removeAt(index)
                photosFragment.updateList()

 */

        val jSONArray = getJSONArrayListSingle(imageOrder)
//                val jsoCvArrayList: String   = jsonObj.toString()

        val piecesCount = jSONArray.length();

        mainAct.runOnUiThread{
            fragment.progressBarPiece.visibility         = View.VISIBLE
            fragment.progressBarPiece.isIndeterminate    = false
            fragment.progressBarPiece.max                = piecesCount-1
            fragment.progressBarPiece.min                = 0
        }

        for (pieceIndex in 0 until piecesCount) {

            mainAct.saveLog("send $pieceIndex   of $piecesCount")

            val jsonObj     = jSONArray.getJSONObject(pieceIndex)
                sendResult  = dl.sendOrder(outputJson, jsonObj)

                mainAct.saveLog(sendResult)

            val uiInfoPiece  = Runnable {

                val ref = FirebaseDatabase.getInstance(MainActivity.FIREINSTANCE).getReference("orders")

                val valueEventListener = object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {

                        fragment.progressBarPiece.progress   = snapshot.childrenCount.toInt()

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                }

                ref.child(session).child(imageOrder.uuid).addListenerForSingleValueEvent(valueEventListener)

//                        fragment.progressBarPiece.progress   = pieceIndex

            }

            mainAct.runOnUiThread(uiInfoPiece)

        }

        if (thisIsLastOne)  workWithResult(sendResult, fragment)

        return sendResult

/*
                GlobalScope.launch(Dispatchers.Main) {
                    //val deferred              = async(Dispatchers.Default) { sendPhoto(index, imageOrder, dl , outputJson ) }
                    sendResult             = withContext(Dispatchers.Default) { sendPhoto(index, imageOrder, dl , outputJson ) }



                    val toast = Toast(mainAct)

                    mainAct.progressBar.visibility = ProgressBar.VISIBLE
                    mainAct.progressBar.progress = index
                    mainAct.progressBar.isIndeterminate = true

                    toast.setText(index.toString())

                    toast.show()

                }
                \
 */



    }

     fun workWithResult(sendResult:String, fragment: OrderFragment){
        try {

            val resultJSSONObj  = JSONObject(sendResult)
            val mValues         = resultJSSONObj//.getJSONObject("mValues")
            result              = resultJSSONObj.toString()

            //mainAct.log.add("result = $result")
            mainAct.saveLog("result = $result")

            name            = mValues.getString("orderName")
            orderStatus     = mValues.getString("orderStatus")
//                    uuid            = mValues.getString("orderUuid")
            orderSendResult = name
            status          = SENT

            mainAct.runOnUiThread {
                fragment.arguments?.putString("orderName"   , name)
                fragment.arguments?.putString("orderStatus" , orderStatus)
                fragment.arguments?.putString("orderUuid"   , mValues.getString("orderUuid"))

                fragment.fillBySend(fragment.requireView().rootView)
                //  progressBar.progress
            }

            mainAct.sendNotification(null, resultJSSONObj)
        }
        catch (e: Exception) {

            // тууут ошибка загрузки заказа
            result = sendResult
            status = SEND_ERROR

            //mainAct.log.add("result = $result")
            mainAct.saveLog("result = $result")
        }

    }

    private fun sendImageOrderByCoroutinesAsync(imageOrder: ImageOrder, index : Int , fragment: OrderFragment): Deferred<Unit> = scope.async {

        sendImageOrderAsync(imageOrder, index , fragment)

    }

    private fun sendByCoroutines(fragment: OrderFragment) {

//        mainAct.saveLog("send by coroutine begin")

        var sendResult = ""

        mainAct.runOnUiThread{
            fragment.progressBar.visibility         = View.VISIBLE
            fragment.progressBar.isIndeterminate    = false
            fragment.progressBar.max                = imageOrderList.size-1
            fragment.progressBar.min                = 0
        }


        val ref                 = FirebaseDatabase
                                    .getInstance(MainActivity.FIREINSTANCE)
                                    .getReference("orders")
                                    .child(session)

        val listener = object :ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                when (snapshot.key) {
                    "sendResult" -> {} // workWithResult(snapshot.value.toString() , fragment)
//                    {
//                        when (snapshot.value.toString().toBoolean()){
//                            true ->

//                                workWithResult(snapshot.value.toString() , fragment)

                           else -> {}
                        }
//                    }
//                    "sendResult" -> sendResult = snapshot.value.toString()
                }



            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                when (snapshot.key) {
                    "receivedOrder" -> {
                        when (snapshot.value.toString().toBoolean()){
                            true -> workWithResult(sendResult , fragment)
                            else -> {}
                        }
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

        ref.addChildEventListener(listener)



//        val valueEventListener  = object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                when (snapshot.value){
//                    true -> workWithResult(sendResult , fragment)
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        }
//
//        ref.child(session).child("receivedOrder").addListenerForSingleValueEvent(valueEventListener)

//        reference1!!.addChildEventListener(object : ChildEventListener {
//            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String) {
//                val map = dataSnapshot.getValue<Map<*, *>>(Map<*, *>::class.java)
//                val message = map.get("message").toString()
//                val userName = map.get("user").toString()



        imageOrderList.forEachIndexed  { index, imageOrder ->

            val uiInfo  = Runnable {

                fragment.textViewResult.text    = String.format(mainAct.resources.getString(R.string.sending), index + 1, imageOrderList.size);
                fragment.progressBar.progress   = index

            }



            scope.launch() {

                val deferred = sendImageOrderByCoroutinesAsync(imageOrder, index , fragment)
//                deferred.await()
                deferred.start()
                mainAct.runOnUiThread(uiInfo)
            }
        }





//        val ref                 = FirebaseDatabase.getInstance(MainActivity.FIREINSTANCE).getReference("orders")
//        val valueEventListener  = object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                when (snapshot.child("receivedOrder").value.toString().toBoolean()){
//                    true -> workWithResult(sendResult , fragment)
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        }
//        ref.child(session).addListenerForSingleValueEvent(valueEventListener)


//        ref.child(session).child("receivedOrder").addListenerForSingleValueEvent(valueEventListener)

//        mainAct.saveLog("send coroutine end")

    }

    private fun getSendThread(fragment: OrderFragment) : Thread {
        return Thread {

            mainAct.saveLog("send thread create")

//            val jsonObject              = getJSONForWs()
//            val dl                      = DataLoader()
//            val outputJson              = jsonObject.toString()

/*
            mainAct.runOnUiThread(Runnable {
                mainAct.progressBar.max = imageOrderList.size-1
                mainAct.progressBar.min = 0
                mainAct.progressBar.isIndeterminate = false

                mainAct.progressBar.visibility =  ProgressBar.VISIBLE
            })

 */
/*
            val layout = findView
            val progressBar = ProgressBar(mainAct null, android.R.attr.progressBarStyleLarge)
            val params = RelativeLayout.LayoutParams(100, 100)
            params.addRule(RelativeLayout.CENTER_IN_PARENT)
            layout.addView(progressBar, params)

 */

           // val cvArrayList             = getJSONArrayList()
            //val jsoCvArrayList: String  = cvArrayList.toString()
            //val sendResult              = dl.sendOrder(outputJson, jsoCvArrayList)

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

//            workWithResult(sendResult, fragment)

//            try {
//
//                    val resultJSSONObj  = JSONObject(sendResult)
//                    val mValues         = resultJSSONObj.getJSONObject("mValues")
//                    result              = resultJSSONObj.toString()
//
//                    //mainAct.log.add("result = $result")
//                    mainAct.saveLog("result = $result")
//
//                    name            = mValues.getString("orderName")
//                    orderStatus     = mValues.getString("orderStatus")
////                    uuid            = mValues.getString("orderUuid")
//                    orderSendResult = name
//                    status          = SENT
//
//                    mainAct.runOnUiThread {
//                        fragment.arguments?.putString("orderName"   , name)
//                        fragment.arguments?.putString("orderStatus" , orderStatus)
//                        fragment.arguments?.putString("orderUuid"   ,  mValues.getString("orderUuid"))
//
//                        fragment.fillBySend(fragment.requireView().rootView)
//                        //  progressBar.progress
//                    }
//                }
//            catch (e: Exception) {
//
//                // тууут ошибка загрузки заказа
//                result = sendResult
//                status = SEND_ERROR
//
//                //mainAct.log.add("result = $result")
//                mainAct.saveLog("result = $result")
//                }
//------------------------------------------------------------------
           // fragment.progressBar.visibility  = ProgressBar.GONE

            /*
            when (this.indexInPacket == this.countOfPacket ){
                true -> {

                    val bundle = Bundle()

                    bundle.putBoolean("sendorder"   , true)
                    bundle.putString("orderName"    , name)
                    bundle.putString("orderStatus"  , orderStatus)
                    bundle.putString("orderUuid"    , uuid)



                    //mainAct.findNavController(R.id.fragment).navigate(R.id.orderFragment, bundle)

                }
            }

             */
        }
    }



    fun send (fragment: OrderFragment){

        when (imageOrderList.size){
            0 -> {
                // nothing
                //mainAct.log.add("nothing to send ")
                mainAct.saveLog("nothing to send ")
            }
            else ->{

//                val sendThread = getSendThread(fragment)
//
//                sendThread.start()

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

              //  when (this.indexInPacket == this.countOfPacket ){
                //    true -> {
                  //  }
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