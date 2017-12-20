package directory.tripin.com.tripindirectory.activity

import android.support.v7.app.AppCompatActivity


class HomeActivity : AppCompatActivity(){

//    var mDrawerLayout: DrawerLayout? = null
//    var mSearchView: FloatingSearchView? = null
//    var mSearchTagRadioGroup: RadioGroup? = null
//    var mRouteSearchTag: RadioButton? = null
//    var mTransportSearchTag: RadioButton? = null
//    var query: Query? = null
//    private var adapter: FirestoreRecyclerAdapter<*, *>? = null
//    private var mPartnerList: RecyclerView? = null
//    private var options: FirestoreRecyclerOptions<PartnerInfoPojo>? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_home)
//        init()
//        viewSetup()
//    }
//
//    fun init() {
//        mPartnerList = findViewById(R.id.transporter_list)
//        mPartnerList?.setLayoutManager(LinearLayoutManager(this))
//
//        mSearchTagRadioGroup = findViewById(R.id.search_tag_group)
//        mDrawerLayout =  findViewById(R.id.drawer_layout)
//        mSearchView = findViewById(R.id.floating_search_view)
//        mSearchView?.attachNavigationDrawerToMenuButton(mDrawerLayout as DrawerLayout)
//
//<<<<<<< HEAD
//=======
//        nav_view.setNavigationItemSelectedListener(this)
//>>>>>>> b8158de2dfd7546b5cea5724381ccb20906b6965
//    }
//
//    fun viewSetup() {
//        mSearchTagRadioGroup?.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, radioButtonID ->
//            if (radioButtonID == R.id.search_by_route) {
//                mSearchView?.setSearchHint("Source To Destination")
//            } else if (radioButtonID == R.id.search_by_transporter) {
//            mSearchView?.setSearchHint("Search by transporter name")
//            } else if (radioButtonID == R.id.search_by_people) {
//                mSearchView?.setSearchHint("Search by people name")
//            }
//        })
//
//        query = FirebaseFirestore.getInstance()
//                .collection("partners")
//        options = FirestoreRecyclerOptions.Builder<PartnerInfoPojo>()
//                .setQuery(query, PartnerInfoPojo::class.java).build()
//        adapter = object : FirestoreRecyclerAdapter<PartnerInfoPojo, PartnersViewHolder>(options) {
//            public override fun onBindViewHolder(holder: PartnersViewHolder, position: Int, model: PartnerInfoPojo) {
//<<<<<<< HEAD
//                holder.mAddress.text = model.getmCompanyAdderss().getAddress()
//=======
//                holder.mAddress.text = model.getmCompanyAdderss().getmAddress()
//>>>>>>> b8158de2dfd7546b5cea5724381ccb20906b6965
//                holder.mCompany.text = model.getmCompanyName()
//            }
//
//            override fun onCreateViewHolder(group: ViewGroup, i: Int): PartnersViewHolder {
//                val view = LayoutInflater.from(group.context)
//                        .inflate(R.layout.single_partner_row1, group, false)
//                return PartnersViewHolder(view)
//            }
//
//            override fun onDataChanged() {
//                super.onDataChanged()
//                Logger.v("on Data changed")
//            }
//        }
//
//        mPartnerList?.setAdapter(adapter)
//
//    }
//<<<<<<< HEAD
////    override fun onBackPressed() {
////        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
////            drawer_layout.closeDrawer(GravityCompat.START)
////        } else {
////            super.onBackPressed()
////        }
////    }
//=======
//    override fun onBackPressed() {
//        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
//            drawer_layout.closeDrawer(GravityCompat.START)
//        } else {
//            super.onBackPressed()
//        }
//    }
//>>>>>>> b8158de2dfd7546b5cea5724381ccb20906b6965
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.home, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        when (item.itemId) {
//            R.id.action_settings -> return true
//            else -> return super.onOptionsItemSelected(item)
//        }
//    }
//
//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        // Handle navigation view item clicks here.
//        when (item.itemId) {
//            R.id.nav_camera -> {
//                // Handle the camera action
//            }
//            R.id.nav_gallery -> {
//
//            }
//            R.id.nav_slideshow -> {
//
//            }
//            R.id.nav_manage -> {
//
//            }
//            R.id.nav_share -> {
//
//            }
//            R.id.nav_send -> {
//
//            }
//        }
//
//<<<<<<< HEAD
////        drawer_layout.closeDrawer(GravityCompat.START)
//=======
//        drawer_layout.closeDrawer(GravityCompat.START)
//>>>>>>> b8158de2dfd7546b5cea5724381ccb20906b6965
//        return true
//    }
//
//    private fun setAdapter(s: String) {
//        adapter?.stopListening()
//        adapter?.notifyDataSetChanged()
//
//        //update your query here
//
//        query = FirebaseFirestore.getInstance()
//                .collection("partners")
//
//        if (s != "") {
//            if (s.contains("To")) {
//                Toast.makeText(this, "Contain To", Toast.LENGTH_LONG).show()
//                val sourceDestination = s.split("To".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//                val source = sourceDestination[0].trim { it <= ' ' }
//                val destination = sourceDestination[1].trim { it <= ' ' }
//                query = FirebaseFirestore.getInstance()
//                        .collection("partners").whereEqualTo("mSourceCities." + source, true).whereEqualTo("destinationCities." + destination, true)
//
//            } else {
//                query = FirebaseFirestore.getInstance()
//                        .collection("partners").whereEqualTo("mCompanyName", s)
//            }
//        }
//        options = FirestoreRecyclerOptions.Builder<PartnerInfoPojo>()
//                .setQuery(query, PartnerInfoPojo::class.java)
//                .build()
//
//        adapter = object : FirestoreRecyclerAdapter<PartnerInfoPojo, PartnersViewHolder>(options) {
//            public override fun onBindViewHolder(holder: PartnersViewHolder, position: Int, model: PartnerInfoPojo) {
//<<<<<<< HEAD
//                holder.mAddress.text = model.getmCompanyAdderss().getAddress()
//=======
//                holder.mAddress.text = model.getmCompanyAdderss().getmAddress()
//>>>>>>> b8158de2dfd7546b5cea5724381ccb20906b6965
//                holder.mCompany.text = model.getmCompanyName()
//            }
//
//            override fun onCreateViewHolder(group: ViewGroup, i: Int): PartnersViewHolder {
//                val view = LayoutInflater.from(group.context)
//                        .inflate(R.layout.single_partner_row1, group, false)
//                return PartnersViewHolder(view)
//            }
//
//            override fun onDataChanged() {
//                super.onDataChanged()
//                Logger.v("on Data changed")
//            }
//        }
//
//        mPartnerList?.setAdapter(adapter)
//        adapter?.startListening()
//
//    }
}
