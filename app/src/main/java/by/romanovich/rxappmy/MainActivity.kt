package by.romanovich.rxappmy

import android.os.Bundle
import android.os.Looper

import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import by.romanovich.rxappmy.databinding.ActivityMainBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.RuntimeException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var changeText: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //rx
        binding.buttonRx.setOnClickListener {
            /*val obser = */

             Observable.just(binding.editText.text)//.toString())
            //2 Observable.fromIterable(binding.editText.text.toString().toCharArray().toList())
            //3 Observable.fromCallable()
            //4Single.just(binding.editText.text.toString().toCharArray().toList())
            //5Maybe.just(binding.editText.text.toString())
            //6 завершает Completable.complete()
            //простейшее преобразование
                .map {  it.toString() }
                    //если строка не пустая то пойдет дальше
                .filter { it.isNotBlank() }
                .map {  it.uppercase()}
                .map {  it.reversed()}
                //планировщик для потока, разные потоки
                .observeOn(Schedulers.computation())
                    //doOnNext можно делать чтото на новых данных
                .map {  changerString(it)}
               // .doOnNext{throw RuntimeException("gjjhh")}
               // .doOnNext { Log.d("@@", Thread.currentThread().name) }
                    //для получения данных из сети
                .observeOn(Schedulers.io())
                .map {  appendDate(it)}
                .observeOn(AndroidSchedulers.mainThread())
                //подписаться, метод вызова. Секция onSuccess
                .subscribeBy (
                    //для Single onSuccess
                    //onNext ля Observable
                    onNext = {
                    binding.textView.text = it
                },
                    //завершает цепочку
                    onError = {
                    binding.textView.text = it.message
                },
                onComplete = {
                    binding.textView.text="Finish"
                })
        }

        //kotlin


        binding.button.setOnClickListener{
            val inputText = binding.editText.text.toString()
            val upperText = inputText.uppercase()
            val reversedText = upperText.reversed()
            Thread {
                try {
                    val changeText = changerString(reversedText)
                    Thread {
                        try {
                            val dateText = appendDate(changeText)
                            runOnUiThread {
                                binding.textView.text = dateText
                            }
                        } catch (ie: InterruptedException) {
                            //todo
                        }
                    }.start()
                } catch (ie: InterruptedException){
                    //todo
                }
            }.start()
        }
    }
    @WorkerThread
    private fun changerString(input: String): String {
        val sb = StringBuilder()
        if (Thread.currentThread() == Looper.getMainLooper().thread){
            throw RuntimeException("kujhkj")
        }

        return sb.toString()
    }

    @WorkerThread
    private fun appendDate(input: String): String {
        if (Thread.currentThread() == Looper.getMainLooper().thread){
            throw RuntimeException("kujhkj")
        }
        Thread.sleep(2000)
        val date = Calendar.getInstance().time
        val dateStr= SimpleDateFormat().format(date)
        return "$input $dateStr"
    }


}