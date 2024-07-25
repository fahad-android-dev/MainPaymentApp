import android.content.Context
import android.content.Intent
import com.orbits.paymentapp.mvvm.main.MainActivity


object AppNavigation {

    fun Context.navigateToMain(msg: String = "", block: () -> Unit) {
        val intent = Intent(this, MainActivity::class.java)
        if (msg.isNotEmpty()) intent.putExtra("msg", msg)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        block()
    }



}