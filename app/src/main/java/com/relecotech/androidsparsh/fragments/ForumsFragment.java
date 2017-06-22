package com.relecotech.androidsparsh.fragments;

/**
 * Created by amey on 10/16/2015.
 */
public class ForumsFragment extends android.support.v4.app.Fragment {

//    private Fragment mFragment;
//    private FragmentManager fragmentManager;
//    String user;
//
//    @Override
//    public void onResume() {
//        user = getActivity().getResources().getString(R.string.login_user_role);
//
//        super.onResume();
// /* Below statment for changing Action Bar Title */
 //   ((MainActivity) getActivity()).setActionBarTitle("Forums");
//        getView().setFocusableInTouchMode(true);
//        getView().requestFocus();
//        getView().setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
//
//                    // handle back buttson
//                    if (user.equals("Teacher")) {
//                        mFragment = new DashboardTeacherFragment();
//                        fragmentManager = getFragmentManager();
//                        fragmentManager.beginTransaction().replace(R.id.content_frame, mFragment).addToBackStack(null).commit();
//
//                    }
//                    if (user.equals("Student")) {
//                        mFragment = new DashboardStudentFragment();
//                        fragmentManager = getFragmentManager();
//                        fragmentManager.beginTransaction().replace(R.id.content_frame, mFragment).addToBackStack(null).commit();
//
//                    }
//                    return true;
//
//                }
//
//                return false;
//            }
//        });
//    }
}
