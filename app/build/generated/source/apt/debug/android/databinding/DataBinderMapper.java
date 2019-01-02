
package android.databinding;
import xyz.elfanrodhian.myfamilylocator.BR;
class DataBinderMapper  {
    final static int TARGET_MIN_SDK = 18;
    public DataBinderMapper() {
    }
    public android.databinding.ViewDataBinding getDataBinder(android.databinding.DataBindingComponent bindingComponent, android.view.View view, int layoutId) {
        switch(layoutId) {
                case xyz.elfanrodhian.myfamilylocator.R.layout.activity_main:
                    return xyz.elfanrodhian.myfamilylocator.databinding.ActivityMainBinding.bind(view, bindingComponent);
                case xyz.elfanrodhian.myfamilylocator.R.layout.content_main:
                    return xyz.elfanrodhian.myfamilylocator.databinding.ContentMainBinding.bind(view, bindingComponent);
        }
        return null;
    }
    android.databinding.ViewDataBinding getDataBinder(android.databinding.DataBindingComponent bindingComponent, android.view.View[] views, int layoutId) {
        switch(layoutId) {
        }
        return null;
    }
    int getLayoutId(String tag) {
        if (tag == null) {
            return 0;
        }
        final int code = tag.hashCode();
        switch(code) {
            case 423753077: {
                if(tag.equals("layout/activity_main_0")) {
                    return xyz.elfanrodhian.myfamilylocator.R.layout.activity_main;
                }
                break;
            }
            case 731091765: {
                if(tag.equals("layout/content_main_0")) {
                    return xyz.elfanrodhian.myfamilylocator.R.layout.content_main;
                }
                break;
            }
        }
        return 0;
    }
    String convertBrIdToString(int id) {
        if (id < 0 || id >= InnerBrLookup.sKeys.length) {
            return null;
        }
        return InnerBrLookup.sKeys[id];
    }
    private static class InnerBrLookup {
        static String[] sKeys = new String[]{
            "_all"};
    }
}