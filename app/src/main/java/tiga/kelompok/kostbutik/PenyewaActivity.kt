package tiga.kelompok.kostbutik

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class PenyewaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_penyewa)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bnv)
        val frameLayout = findViewById<View>(R.id.frameLayoutP)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.Pembayaran -> {
                    selectedFragment = FragmentPenyewaPembayaran()
                    frameLayout.visibility = View.VISIBLE
                }
            }

            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayoutP, selectedFragment)
                    .commit()
            }

            true
        }

        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.Pembayaran
        }
    }
}
