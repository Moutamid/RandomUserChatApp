package com.moutamid.randomchat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.randomchat.Adapters.GroupsAdapter;
import com.moutamid.randomchat.Models.GroupsModel;
import com.moutamid.randomchat.databinding.FragmentGroupsBinding;
import com.moutamid.randomchat.utils.Constants;

public class GroupsFragment extends Fragment {

    FragmentGroupsBinding b;
    GroupsAdapter adapter;
    List<GroupsModel> list;
    private String banner = "";
    private Context mContext;

    public GroupsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = MainActivity.context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentGroupsBinding.inflate(getLayoutInflater());
        this.mContext = MainActivity.context;
        list = new ArrayList<>();
        b.rec.setLayoutManager(new LinearLayoutManager(getActivity()));
        setListData();
        if (mContext != null) {
            adapter = new GroupsAdapter(mContext, list);
        }
        b.rec.setAdapter(adapter);
        getIds();
        //getGroupList();
        return b.getRoot();
    }
    private void getIds() {
        Constants.databaseReference().child("AdmobId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    banner=snapshot.child("banner").getValue().toString();
                    AdView adView = new AdView(mContext);
                    adView.setAdSize(AdSize.SMART_BANNER);
                    adView.setAdUnitId(banner);
                    AdRequest request = new AdRequest.Builder().build();
                    adView.loadAd(request);
                    b.adView.addView(adView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void setListData() {
        GroupsModel model1 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Afghanistan","Desc","afg",
                com.hbb20.R.drawable.flag_afghanistan);
        list.add(model1);
        GroupsModel model2 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Albania","Desc","alb",
                com.hbb20.R.drawable.flag_albania);
        list.add(model2);
        GroupsModel model3 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Algeria","Desc","alg",
                com.hbb20.R.drawable.flag_algeria);
        list.add(model3);
        GroupsModel model4 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Andorra","Desc","and",
                com.hbb20.R.drawable.flag_andorra);
        list.add(model4);
        GroupsModel model5 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Angola","Desc","ao",
                com.hbb20.R.drawable.flag_angola);
        list.add(model5);
        GroupsModel model6 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Antigua and Barbuda","Desc","ac",
                com.hbb20.R.drawable.flag_antigua_and_barbuda);
        list.add(model6);
        GroupsModel model7 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Argentina","Desc","ar",
                com.hbb20.R.drawable.flag_argentina);
        list.add(model7);
        GroupsModel model8 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Armenia","Desc","am",
                com.hbb20.R.drawable.flag_armenia);
        list.add(model8);
        GroupsModel model9 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Australia","Desc","as",
                com.hbb20.R.drawable.flag_australia);
        list.add(model9);
        GroupsModel model10 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Austria","Desc","tn_au",
                com.hbb20.R.drawable.flag_austria);
        list.add(model10);
        GroupsModel model11 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Azerbaijan","Desc","aj",
                com.hbb20.R.drawable.flag_azerbaijan);
        list.add(model11);
        GroupsModel model12 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Bahamas","Desc","bf",
                com.hbb20.R.drawable.flag_bahamas);
        list.add(model12);
        GroupsModel model13 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Bahrain","Desc","ba",
                com.hbb20.R.drawable.flag_bahrain);
        list.add(model13);
        GroupsModel model14 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Bangladesh","Desc","bg",
                com.hbb20.R.drawable.flag_bangladesh);
        list.add(model14);
        GroupsModel model15 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Barbados","Desc","bb",
                com.hbb20.R.drawable.flag_barbados);
        list.add(model15);
        GroupsModel model16 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Belarus","Desc","bo",
                com.hbb20.R.drawable.flag_belarus);
        list.add(model16);
        GroupsModel model17 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Belgium","Desc","be",
                com.hbb20.R.drawable.flag_belgium);
        list.add(model17);
        GroupsModel model18 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Belize","Desc","bh",
                com.hbb20.R.drawable.flag_belize);
        list.add(model18);
        GroupsModel model19 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Benin","Desc","bn",
                com.hbb20.R.drawable.flag_benin);
        list.add(model19);
        GroupsModel model20 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Bhutan","Desc","tn_bt",
                com.hbb20.R.drawable.flag_bhutan);
        list.add(model20);
        GroupsModel model21 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Bolivia","Desc","tn_bl",
                com.hbb20.R.drawable.flag_bolivia);
        list.add(model21);
        GroupsModel model22 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Bosnia and Herzegovina","Desc","bk",
                com.hbb20.R.drawable.flag_bosnia);
        list.add(model22);
        GroupsModel model23 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Botswana","Desc","bc",
                com.hbb20.R.drawable.flag_botswana);
        list.add(model23);
        GroupsModel model24 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Brazil","Desc","br",
                com.hbb20.R.drawable.flag_brazil);
        list.add(model24);
        GroupsModel model25 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Brunei","Desc","bx",
                com.hbb20.R.drawable.flag_brunei);
        list.add(model25);
        GroupsModel model26 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Bulgaria","Desc","bu",
                com.hbb20.R.drawable.flag_bulgaria);
        list.add(model26);
        GroupsModel model27 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Burkina Faso","Desc","uv",
                com.hbb20.R.drawable.flag_burkina_faso);
        list.add(model27);
        GroupsModel model28 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Burundi","Desc","by",
                com.hbb20.R.drawable.flag_burundi);
        list.add(model28);
        GroupsModel model29 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Côte d'Ivoire","Desc","iv",
                com.hbb20.R.drawable.flag_cote_divoire);
        list.add(model29);
        GroupsModel model30 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Cabo Verde","Desc","cv",
                com.hbb20.R.drawable.flag_cape_verde);
        list.add(model30);
        GroupsModel model31 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Cambodia","Desc","cb",
                com.hbb20.R.drawable.flag_cambodia);
        list.add(model31);
        GroupsModel model32 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Cameroon","Desc","cm",
                com.hbb20.R.drawable.flag_cameroon);
        list.add(model32);
        GroupsModel model33 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Canada","Desc","ca",
                com.hbb20.R.drawable.flag_canada);
        list.add(model33);
        GroupsModel model34 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "CAR","Desc","ct",
                com.hbb20.R.drawable.flag_cuba);
        list.add(model34);
        GroupsModel model35 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Chad","Desc","cd",
                com.hbb20.R.drawable.flag_chad);
        list.add(model35);
        GroupsModel model36 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Chile","Desc","tn_ci",
                com.hbb20.R.drawable.flag_chile);
        list.add(model36);

        GroupsModel model37 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "China","Desc","ch",
                com.hbb20.R.drawable.flag_china);
        list.add(model37);
        GroupsModel model38 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Colombia","Desc","co",
                com.hbb20.R.drawable.flag_colombia);
        list.add(model38);
        GroupsModel model39 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Comoros","Desc","cn",
                com.hbb20.R.drawable.flag_comoros);
        list.add(model39);
        GroupsModel model40 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Congo","Desc","cg",
                com.hbb20.R.drawable.flag_cocos);
        list.add(model40);
        GroupsModel model41 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Costa Rica","Desc","cs",
                com.hbb20.R.drawable.flag_costa_rica);
        list.add(model41);
        GroupsModel model42 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Croatia","Desc","hr",
                com.hbb20.R.drawable.flag_croatia);
        list.add(model42);
        GroupsModel model43 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Cuba","Desc","cu",
                com.hbb20.R.drawable.flag_cuba);
        list.add(model43);
        GroupsModel model44 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Cyprus","Desc","cy",
                com.hbb20.R.drawable.flag_cyprus);
        list.add(model44);
        GroupsModel model45 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Czechia","Desc","ez",
                com.hbb20.R.drawable.flag_czech_republic);
        list.add(model45);
        GroupsModel model46 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Denmark","Desc","da",
                com.hbb20.R.drawable.flag_denmark);
        list.add(model46);
        GroupsModel model47 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Djibouti","Desc","dj",
                com.hbb20.R.drawable.flag_djibouti);
        list.add(model47);
        GroupsModel model48 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Dominica","Desc","do",
                com.hbb20.R.drawable.flag_dominica);
        list.add(model48);
        GroupsModel model49 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Dominican Republic","Desc","dr",
                com.hbb20.R.drawable.flag_dominican_republic);
        list.add(model49);

        GroupsModel model52 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Ecuador","Desc","ec",
                com.hbb20.R.drawable.flag_ecuador);
        list.add(model52);
        GroupsModel model53 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Egypt","Desc","eg",
                com.hbb20.R.drawable.flag_egypt);
        list.add(model53);
        GroupsModel model54 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "El Salvador","Desc","es",
                com.hbb20.R.drawable.flag_el_salvador);
        list.add(model54);
        GroupsModel model55 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Equatorial Guinea","Desc","ek",
                com.hbb20.R.drawable.flag_equatorial_guinea);
        list.add(model55);
        GroupsModel model56 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Eritrea","Desc","er",
                com.hbb20.R.drawable.flag_eritrea);
        list.add(model56);
        GroupsModel model57 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Estonia","Desc","en",
                com.hbb20.R.drawable.flag_estonia);
        list.add(model57);
        GroupsModel model59 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Ethiopia","Desc","et",
                com.hbb20.R.drawable.flag_ethiopia);
        list.add(model59);
        GroupsModel model60 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Fiji","Desc","fj",
                com.hbb20.R.drawable.flag_fiji);
        list.add(model60);
        GroupsModel model61 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Finland","Desc","fi",
                com.hbb20.R.drawable.flag_finland);
        list.add(model61);
        GroupsModel model62 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "France","Desc","fr",
                com.hbb20.R.drawable.flag_france);
        list.add(model62);

        GroupsModel model63 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Gabon","Desc","gb",
                com.hbb20.R.drawable.flag_gabon);
        list.add(model63);
        GroupsModel model64 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Gambia","Desc","ga",
                com.hbb20.R.drawable.flag_gambia);
        list.add(model64);
        GroupsModel model65 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Georgia","Desc","gg",
                com.hbb20.R.drawable.flag_georgia);
        list.add(model65);
        GroupsModel model66 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Germany","Desc","gm",
                com.hbb20.R.drawable.flag_germany);
        list.add(model66);
        GroupsModel model67 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Ghana","Desc","gh",
                com.hbb20.R.drawable.flag_ghana);
        list.add(model67);
        GroupsModel model68 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Greece","Desc","gr",
                com.hbb20.R.drawable.flag_greece);
        list.add(model68);
        GroupsModel model69 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Grenada","Desc","gj",
                com.hbb20.R.drawable.flag_grenada);
        list.add(model69);
        GroupsModel model70 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Guatemala","Desc","gt",
                com.hbb20.R.drawable.flag_guatemala);
        list.add(model70);
        GroupsModel model71 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Guinea","Desc","gv",
                com.hbb20.R.drawable.flag_guinea);
        list.add(model71);
        GroupsModel model72 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Guinea-Bissau","Desc","pu",
                com.hbb20.R.drawable.flag_guinea_bissau);
        list.add(model72);
        GroupsModel model73 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Guyana","Desc","gy",
                com.hbb20.R.drawable.flag_guyana);
        list.add(model73);
        GroupsModel model74 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Haiti","Desc","ha",
                com.hbb20.R.drawable.flag_haiti);
        list.add(model74);
        GroupsModel model76 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Honduras","Desc","ho",
                com.hbb20.R.drawable.flag_honduras);
        list.add(model76);
        GroupsModel model77 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Hungary","Desc","hu",
                com.hbb20.R.drawable.flag_hungary);
        list.add(model77);
        GroupsModel model78 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Iceland","Desc","ic",
                com.hbb20.R.drawable.flag_iceland);
        list.add(model78);
        GroupsModel model79 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "India","Desc","in",
                com.hbb20.R.drawable.flag_india);
        list.add(model79);
        GroupsModel model80 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Indonesia","Desc","id",
                com.hbb20.R.drawable.flag_indonesia);
        list.add(model80);
        GroupsModel model81 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Iran","Desc","ir",
                com.hbb20.R.drawable.flag_iran);
        list.add(model81);
        GroupsModel model82 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Iraq","Desc","iz",
                com.hbb20.R.drawable.flag_iraq);
        list.add(model82);
        GroupsModel model83 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Ireland","Desc","ei",
                com.hbb20.R.drawable.flag_ireland);
        list.add(model83);
        GroupsModel model84 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Israel","Desc","is",
                com.hbb20.R.drawable.flag_israel);
        list.add(model84);
        GroupsModel model85 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Italy","Desc","it",
                com.hbb20.R.drawable.flag_italy);
        list.add(model85);
        GroupsModel model86 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Jamaica","Desc","jm",
                com.hbb20.R.drawable.flag_jamaica);
        list.add(model86);
        GroupsModel model87 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Japan","Desc","ja",
                com.hbb20.R.drawable.flag_japan);
        list.add(model87);
        GroupsModel model88 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Jordan","Desc","jo",
                com.hbb20.R.drawable.flag_jordan);
        list.add(model88);
        GroupsModel model89 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Kazakhstan","Desc","kaz",
                com.hbb20.R.drawable.flag_kazakhstan);
        list.add(model89);
        GroupsModel model90 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Kenya","Desc","ken",
                com.hbb20.R.drawable.flag_kenya);
        list.add(model90);
        GroupsModel model91 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Kiribati","Desc","kr",
                com.hbb20.R.drawable.flag_kiribati);
        list.add(model91);
        GroupsModel model92 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Kuwait","Desc","ku",
                com.hbb20.R.drawable.flag_kuwait);
        list.add(model92);
        GroupsModel model93 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Kyrgyzstan","Desc","kg",
                com.hbb20.R.drawable.flag_kyrgyzstan);
        list.add(model93);
        GroupsModel model94 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Laos","Desc","la",
                com.hbb20.R.drawable.flag_laos);
        list.add(model94);
        GroupsModel model95 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Latvia","Desc","lg",
                com.hbb20.R.drawable.flag_latvia);
        list.add(model95);
        GroupsModel model96 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Lebanon","Desc","le",
                com.hbb20.R.drawable.flag_lebanon);
        list.add(model96);
        GroupsModel model97 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Lesotho","Desc","lt",
                com.hbb20.R.drawable.flag_lesotho);
        list.add(model97);
        GroupsModel model98 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Liberia","Desc","li",
                com.hbb20.R.drawable.flag_liberia);
        list.add(model98);
        GroupsModel model99 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Libya","Desc","ly",
                com.hbb20.R.drawable.flag_libya);
        list.add(model99);
        GroupsModel model100 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Liechtenstein","Desc","ls",
                com.hbb20.R.drawable.flag_liechtenstein);
        list.add(model100);
        GroupsModel model101 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Lithuania","Desc","lh",
                com.hbb20.R.drawable.flag_lithuania);
        list.add(model101);
        GroupsModel model102 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Luxembourg","Desc","lux",
                com.hbb20.R.drawable.flag_luxembourg);
        list.add(model102);
        GroupsModel model103 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Madagascar","Desc","mad",
                com.hbb20.R.drawable.flag_madagascar);
        list.add(model103);
        GroupsModel model104 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Malawi","Desc","mi",
                com.hbb20.R.drawable.flag_malawi);
        list.add(model104);
        GroupsModel model105 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Malaysia","Desc","my",
                com.hbb20.R.drawable.flag_malaysia);
        list.add(model105);
        GroupsModel model106 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Maldives and Barbuda","Desc","mv",
                com.hbb20.R.drawable.flag_maldives);
        list.add(model106);
        GroupsModel model107 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Mali","Desc","ml",
                com.hbb20.R.drawable.flag_mali);
        list.add(model107);
        GroupsModel model108 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Malta","Desc","mt",
                com.hbb20.R.drawable.flag_malta);
        list.add(model108);
        GroupsModel model109 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Marshall Islands","Desc","rm",
                com.hbb20.R.drawable.flag_marshall_islands);
        list.add(model109);
        GroupsModel model110 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Mauritania","Desc","mr",
                com.hbb20.R.drawable.flag_mauritania);
        list.add(model110);
        GroupsModel model111 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Mauritius","Desc","mp",
                com.hbb20.R.drawable.flag_mauritius);
        list.add(model111);
        GroupsModel model112 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Mexico","Desc","mx",
                com.hbb20.R.drawable.flag_mexico);
        list.add(model112);
        GroupsModel model113 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Micronesia","Desc","fm",
                com.hbb20.R.drawable.flag_micronesia);
        list.add(model113);
        GroupsModel model114 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Moldova","Desc","mol",
                com.hbb20.R.drawable.flag_moldova);
        list.add(model114);
        GroupsModel model115 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Monaco","Desc","mn",
                com.hbb20.R.drawable.flag_monaco);
        list.add(model115);
        GroupsModel model116 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Mongolia","Desc","mon",
                com.hbb20.R.drawable.flag_mongolia);
        list.add(model116);
        GroupsModel model117 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Montenegro","Desc","moj",
                com.hbb20.R.drawable.flag_of_montenegro);
        list.add(model117);
        GroupsModel model118 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Morocco","Desc","mor",
                com.hbb20.R.drawable.flag_morocco);
        list.add(model118);
        GroupsModel model119 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Mozambique","Desc","moz",
                com.hbb20.R.drawable.flag_mozambique);
        list.add(model119);
        GroupsModel model120 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Myanmar","Desc","mya",
                com.hbb20.R.drawable.flag_myanmar);
        list.add(model120);
        GroupsModel model121 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Namibia","Desc","wa",
                com.hbb20.R.drawable.flag_namibia);
        list.add(model121);
        GroupsModel model122 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Nauru","Desc","nr",
                com.hbb20.R.drawable.flag_nauru);
        list.add(model122);
        GroupsModel model123 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Nepal","Desc","np",
                com.hbb20.R.drawable.flag_nepal);
        list.add(model123);
        GroupsModel model124 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Netherlands","Desc","nl",
                com.hbb20.R.drawable.flag_netherlands);
        list.add(model124);
        GroupsModel model125 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "New Zealand","Desc","nz",
                com.hbb20.R.drawable.flag_new_zealand);
        list.add(model125);
        GroupsModel model126 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Nicaragua","Desc","nu",
                com.hbb20.R.drawable.flag_nicaragua);
        list.add(model126);
        GroupsModel model127 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Niger","Desc","ng",
                com.hbb20.R.drawable.flag_niger);
        list.add(model127);
        GroupsModel model128 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Nigeria","Desc","nig",
                com.hbb20.R.drawable.flag_nigeria);
        list.add(model128);
        GroupsModel model129 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "North Macedonia","Desc","nor",
                com.hbb20.R.drawable.flag_northern_mariana_islands);
        list.add(model129);
        GroupsModel model130 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Norway","Desc","noy",
                com.hbb20.R.drawable.flag_norway);
        list.add(model130);
        GroupsModel model131 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Oman","Desc","om",
                com.hbb20.R.drawable.flag_oman);
        list.add(model131);
        GroupsModel model132 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Pakistan","Desc","pk",
                com.hbb20.R.drawable.flag_pakistan);
        list.add(model132);
        GroupsModel model133 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Palau","Desc","ps",
                com.hbb20.R.drawable.flag_palau);
        list.add(model133);
        GroupsModel model134 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Panama","Desc","pm",
                com.hbb20.R.drawable.flag_panama);
        list.add(model134);
        GroupsModel model135 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Papua New Guinea","Desc","pp",
                com.hbb20.R.drawable.flag_papua_new_guinea);
        list.add(model135);
        GroupsModel model136 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Paraguay","Desc","par",
                com.hbb20.R.drawable.flag_paraguay);
        list.add(model136);

        GroupsModel model137 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Peru","Desc","pe",
                com.hbb20.R.drawable.flag_peru);
        list.add(model137);
        GroupsModel model138 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Philippines","Desc","rp",
                com.hbb20.R.drawable.flag_philippines);
        list.add(model138);
        GroupsModel model139 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Poland","Desc","pol",
                com.hbb20.R.drawable.flag_poland);
        list.add(model139);
        GroupsModel model140 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Portugal","Desc","pot",
                com.hbb20.R.drawable.flag_portugal);
        list.add(model140);
        GroupsModel model141 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Qatar","Desc","qa",
                com.hbb20.R.drawable.flag_qatar);
        list.add(model141);
        GroupsModel model142 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Romania","Desc","rom",
                com.hbb20.R.drawable.flag_romania);
        list.add(model142);
        GroupsModel model143 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Russia","Desc","rs",
                com.hbb20.R.drawable.flag_russian_federation);
        list.add(model143);
        GroupsModel model144 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Rwanda","Desc","rw",
                com.hbb20.R.drawable.flag_rwanda);
        list.add(model144);
        GroupsModel model145 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Saint Kitts and Nevis","Desc","sc",
                com.hbb20.R.drawable.flag_saint_kitts_and_nevis);
        list.add(model145);
        GroupsModel model146 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Saint Lucia","Desc","ws",
                com.hbb20.R.drawable.flag_saint_lucia);
        list.add(model146);
        GroupsModel model147 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Samoa","Desc","sm",
                com.hbb20.R.drawable.flag_samoa);
        list.add(model147);
        GroupsModel model148 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "San Marino","Desc","tp",
                com.hbb20.R.drawable.flag_san_marino);
        list.add(model148);
        GroupsModel model149 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Sao Tome and Principe","Desc","sao",
                com.hbb20.R.drawable.flag_sao_tome_and_principe);
        list.add(model149);
        GroupsModel model150 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Saudi Arabia","Desc","sau",
                com.hbb20.R.drawable.flag_saudi_arabia);
        list.add(model150);
        GroupsModel model151 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Senegal","Desc","sen",
                com.hbb20.R.drawable.flag_senegal);
        list.add(model151);

        GroupsModel model152 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Serbia","Desc","ri",
                com.hbb20.R.drawable.flag_serbia);
        list.add(model152);
        GroupsModel model153 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Seychelles","Desc","sey",
                com.hbb20.R.drawable.flag_seychelles);
        list.add(model153);
        GroupsModel model154 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Sierra Leone","Desc","sie",
                com.hbb20.R.drawable.flag_sierra_leone);
        list.add(model154);
        GroupsModel model155 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Singapore","Desc","sn",
                com.hbb20.R.drawable.flag_singapore);
        list.add(model155);
        GroupsModel model156 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Slovakia","Desc","slo",
                com.hbb20.R.drawable.flag_slovakia);
        list.add(model156);
        GroupsModel model157 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Slovenia","Desc","si",
                com.hbb20.R.drawable.flag_slovenia);
        list.add(model157);
        GroupsModel model158 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Solomon Islands","Desc","bp",
                com.hbb20.R.drawable.flag_soloman_islands);
        list.add(model158);
        GroupsModel model159 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Somalia","Desc","so",
                com.hbb20.R.drawable.flag_somalia);
        list.add(model159);
        GroupsModel model160 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "South Africa","Desc","sf",
                com.hbb20.R.drawable.flag_south_africa);
        list.add(model160);
        GroupsModel model161 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "South Korea","Desc","ks",
                com.hbb20.R.drawable.flag_south_korea);
        list.add(model161);
        GroupsModel model162 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "South Sudan","Desc","od",
                com.hbb20.R.drawable.flag_south_sudan);
        list.add(model162);

        GroupsModel model163 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Spain","Desc","spa",
                com.hbb20.R.drawable.flag_spain);
        list.add(model163);
        GroupsModel model164 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Sri Lanka","Desc","sri",
                com.hbb20.R.drawable.flag_sri_lanka);
        list.add(model164);
        GroupsModel model165 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "St. Vincent Grenadines","Desc","vc",
                com.hbb20.R.drawable.flag_saint_vicent_and_the_grenadines);
        list.add(model165);
        GroupsModel model166 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "State of Palestine","Desc","palestine",
                com.hbb20.R.drawable.flag_palestine);
        list.add(model166);
        GroupsModel model167 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Sudan","Desc","sud",
                com.hbb20.R.drawable.flag_sudan);
        list.add(model167);
        GroupsModel model168 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Suriname","Desc","ns",
                com.hbb20.R.drawable.flag_suriname);
        list.add(model168);
        GroupsModel model169 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Sweden","Desc","swe",
                com.hbb20.R.drawable.flag_sweden);
        list.add(model169);
        GroupsModel model170 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Switzerland","Desc","sz",
                com.hbb20.R.drawable.flag_switzerland);
        list.add(model170);
        GroupsModel model171 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Syria","Desc","syr",
                com.hbb20.R.drawable.flag_syria);
        list.add(model171);
        GroupsModel model172 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Tajikistan","Desc","taj",
                com.hbb20.R.drawable.flag_tajikistan);
        list.add(model172);
        GroupsModel model173 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Tanzania","Desc","tz",
                com.hbb20.R.drawable.flag_tanzania);
        list.add(model173);
        GroupsModel model174 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Thailand","Desc","th",
                com.hbb20.R.drawable.flag_thailand);
        list.add(model174);
        GroupsModel model175 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Timor-Leste","Desc","tt",
                com.hbb20.R.drawable.flag_timor_leste);
        list.add(model175);
        GroupsModel model176 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Togo","Desc","tog",
                com.hbb20.R.drawable.flag_togo);
        list.add(model176);
        GroupsModel model177 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Tonga","Desc","ton",
                com.hbb20.R.drawable.flag_tonga);
        list.add(model177);
        GroupsModel model178 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Trinidad and Tobago","Desc","td",
                com.hbb20.R.drawable.flag_trinidad_and_tobago);
        list.add(model178);
        GroupsModel model179 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Tunisia","Desc","tun",
                com.hbb20.R.drawable.flag_tunisia);
        list.add(model179);
        GroupsModel model180 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Turkey","Desc","turkey",
                com.hbb20.R.drawable.flag_turkey);
        list.add(model180);
        GroupsModel model181 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Turkmenistan","Desc","tx",
                com.hbb20.R.drawable.flag_turkmenistan);
        list.add(model181);
        GroupsModel model182 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Tuvalu","Desc","tv",
                com.hbb20.R.drawable.flag_tuvalu);
        list.add(model182);
        GroupsModel model183 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "U.A.E.","Desc","ae",
                com.hbb20.R.drawable.flag_uae);
        list.add(model183);
        GroupsModel model184 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "U.K.","Desc","uk",
                com.hbb20.R.drawable.flag_united_kingdom);
        list.add(model184);
        GroupsModel model185 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "U.S.","Desc","us",
                com.hbb20.R.drawable.flag_united_states_of_america);
        list.add(model185);
        GroupsModel model186 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Uganda","Desc","ug",
                com.hbb20.R.drawable.flag_uganda);
        list.add(model186);
        GroupsModel model187 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Ukraine","Desc","up",
                com.hbb20.R.drawable.flag_ukraine);
        list.add(model187);
        GroupsModel model188 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Uruguay","Desc","uru",
                com.hbb20.R.drawable.flag_uruguay);
        list.add(model188);
        GroupsModel model189 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Uzbekistan","Desc","uzb",
                com.hbb20.R.drawable.flag_uzbekistan);
        list.add(model189);
        GroupsModel model190 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Vanuatu","Desc","van",
                com.hbb20.R.drawable.flag_vanuatu);
        list.add(model190);
        GroupsModel model191 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Venezuela","Desc","ven",
                com.hbb20.R.drawable.flag_venezuela);
        list.add(model191);
        GroupsModel model192 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Vietnam","Desc","vm",
                com.hbb20.R.drawable.flag_vietnam);
        list.add(model192);
        GroupsModel model193 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Yemen","Desc","ym",
                com.hbb20.R.drawable.flag_yemen);
        list.add(model193);
        GroupsModel model194 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Zambia","Desc","zam",
                com.hbb20.R.drawable.flag_zambia);
        list.add(model194);
        GroupsModel model195 = new GroupsModel(Constants.DEFAULT_PROFILE_URL,
                "Zimbabwe","Desc","zim",
                com.hbb20.R.drawable.flag_zimbabwe);
        list.add(model195);

    }

}