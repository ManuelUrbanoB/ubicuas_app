package software.ubicuas.ubicuasfinal

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_principal.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class PrincipalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        btn_ingresar.setOnClickListener {
            if(edt_nickname.text.toString() != "") {
                startActivity<MainActivity>("nick" to edt_nickname.text.toString())
                finish()
            }else{
                toast("tienes que ingresar un apodo para poder seguir")
            }
        }
    }
}
