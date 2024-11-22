package io.github.winemu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import io.github.winemu.R

class PreferenceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent

        setContentView(R.layout.activity_preference)
        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!intent.hasExtra("fragment")) {
            finish()
            return
        }

        try {
            val clazz = classLoader.loadClass(intent.getStringExtra("fragment"))
            val fragment = clazz.newInstance() as Fragment
            fragment.arguments = intent.extras
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commitNow()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    companion object {
        fun launch(context: Context, clazz: Class<out Fragment>) {
            launch(context, clazz, Intent())
        }

        fun launch(context: Context, clazz: Class<out Fragment>, extras: Intent) {
            context.startActivity(
                Intent(context, PreferenceActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtras(extras)
                    .putExtra("fragment", clazz.name)
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
