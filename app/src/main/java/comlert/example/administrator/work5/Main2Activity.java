package comlert.example.administrator.work5;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Vector;

public class Main2Activity extends Activity {

    private final String[]FILE_MapTable = {".3gp",".mov",".avi",".rmvb",".wmv",".mp3",".mp4"};
    private Vector<String> items= null;
    private Vector<String> paths=null;
    private Vector<String> sizes=null;
    private String rootpath="/mnt/sdcard";
    private EditText pathEditText;
    private Button queryButton;
    private ListView fileListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //从myfile.xml中找到相应的元素
        pathEditText = (EditText) findViewById(R.id.path_edit);
        queryButton = (Button) findViewById(R.id.qry_button);
        fileListView = (ListView) findViewById(R.id.file_listview);
        //查询按钮事件
        queryButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file=new File(pathEditText.getText().toString());
                if (file.exists()){
                    if (file.isFile()){
                        //如果是文件就打开播放
                        openFlie(pathEditText.getText().toString());
                    }else{
                        //如果是目录，就打开目录下面的文件
                        getFileDir(pathEditText.getText().toString());
                    }
                }else{
                    Toast.makeText(Main2Activity.this,"找不到该位置",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //设置ListItem被点击时要做的动作
      fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
              fileOrDir(paths.get(position));
          }
      });
        //打开默认文件夹
        getFileDir(rootpath);
    }
    /***重写返回键功能，返回上一级文件**/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            pathEditText = (EditText) findViewById(R.id.path_edit);
            File file=new File(pathEditText.getText().toString());
            if (rootpath.equals(pathEditText.getText().toString().trim())){
                return super.onKeyDown(keyCode,event);
            }else {
                //如果不是back，正常响应
                getFileDir(file.getParent());
                return true;
            }
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /*处理文件或者目录的方法*/
    private void fileOrDir(String path){
        File file=new File(path);
        if (file.isDirectory()){
            getFileDir(file.getPath());
        }else{
            openFlie(path);
        }
    }
    //获取文件结构的方法
    private void getFileDir(String path) {
        pathEditText.setText(path);
        items=new Vector<String>();
        paths = new Vector<String>();
        sizes = new Vector<String>();

        File f=new File(path);
        File[] files = f.listFiles();
        if (files!=null){
            /*将所有文件添加到ArrayList中*/
            for (int i = 0;i<files.length;i++){
                if (files[i].isDirectory()){
                    items.add(files[i].getName());
                    paths.add(files[i].getPath());
                    sizes.add("");
                }
            }

            for (int i=0;i<files.length;i++){
                if (files[i].isFile()){
                    String fileName=files[i].getName();
                    int index = fileName.lastIndexOf(".");
                    if (index>0){
                        String endName = fileName.substring(index,fileName.length()).toLowerCase();
                        String type = null;
                        for (int x =0; x<FILE_MapTable.length;x++){
                            //支持的格式才会在文件浏览器中显式
                            if (endName.equals(FILE_MapTable[x])){
                                type=FILE_MapTable[x];
                                break;
                            }
                        }
                        if (type!=null){
                            items.add(files[i].getName());
                            paths.add(files[i].getPath());
                            sizes.add(files[i].length()+"");
                        }
                    }
                }
            }
        }
        /*使用自定义的FileListAdapter来将数据传入ListView*/
        fileListView.setAdapter(new FileListAdapter(this,items));
    }

    class FileListAdapter extends BaseAdapter{

        private Vector<String> items=null;//items,存放显示的名称
        private Main2Activity myFile;
        public FileListAdapter(Main2Activity myFile,Vector<String> items){
            this.items=items;
            this.myFile=myFile;
        }
        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.elementAt(position);
        }
        @Override
        public long getItemId(int position) {
            return items.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null){
                //加载列表项布局file_item.xml
                convertView = myFile.getLayoutInflater().inflate(R.layout.file_item,null);
            }
            //文件名称
            TextView name = (TextView) convertView.findViewById(R.id.name);
            //媒体文件名称
            ImageView music = (ImageView) convertView.findViewById(R.id.music);
            //文件类型
            ImageView folder = (ImageView) convertView.findViewById(R.id.folder);

            name.setText(items.elementAt(position));
            if (sizes.elementAt(position).equals("")){
                //隐藏媒体图标，显示文件夹图标
                music.setVisibility(View.GONE);
                folder.setVisibility(View.VISIBLE);
            }else{
                //隐藏文件夹图标，显式媒体图标
                music.setVisibility(View.VISIBLE);
                folder.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    private void openFlie(String path) {
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
