package com.anc.allinone.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.anc.allinone.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 只做基本的使用，了解recyclerview的运行原理，未一一适配case
 */

public class RecyclerView extends ViewGroup {

    private final static  String TAG="xjt";

    //官方也是获取的最小滑动距离
    private  int mMinSlop;

    private  boolean mNeedRelayout;

    private Adapter mAdapter;

    //回收管理
    private Recycler mRecycler;

    //省事
    private int typeId=1001;

    private int heightId=1002;

    //时刻记录的Y值
    private float mCurrentY;

    private int mScrollY;

    // view的第一行  的postion
    private int firstRow;

    //官方 scraplist放了Recycler 解耦 但是操作麻烦，简单化，存储界面view（viewHolder--内部持有view）
    //主要目的 首位需要加入到recycler
    private  ArrayList<View> mViewList=new ArrayList<>();

    public RecyclerView(Context context) {
        super(context);
    }

    public RecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }


    public RecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void init(Context context, AttributeSet attrs) {
        ViewConfiguration cfg=ViewConfiguration.get(context);
        mMinSlop=cfg.getScaledTouchSlop();
    }

    /**
     * 步骤3
     * @param adapter
     */
    void setAdapter(Adapter adapter){
        mAdapter=adapter;
        mNeedRelayout=true;
        mRecycler=new Recycler();

        mScrollY = 0;
        firstRow = 0;
        requestLayout();

        invalidate();
    }

    /**
     * 步骤1
     * 官方使用LayoutManager去适配，这里不用LayoutManager了
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量 自己
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测量子节点，只有测量子节点，子节点才有measure值，measure和显示大小不是直接关系，直接关系是layout，

        // Rec没有子节点，所以这一步不需要
        // measureChildren(widthMeasureSpec,heightMeasureSpec);

        //根据子节点测量值+margine+适配wrapcontent去重新计算自己的measure-也不需要
//        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);

//        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);

    }


    /**
     * 步骤2
     * 第一次的核心
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //setadapter或者父布局通知就要relayout
        if(mNeedRelayout || changed) {

            //清空子views
            mNeedRelayout=false;
            mViewList.clear();
            removeAllViews();

            //todo 没有layout完成没有宽高？
            //view的layout 先设置ltrb 然后onlayout 所以自己的width以及有了

            Log.d(TAG, "onLayout: width="+getHeight());

            //逐行添加
            int top=0,bottom=0;
            for(int i=0;i<mAdapter.getCount()&&top<getHeight();i++){
                View  view=obtionAndAddView(i);
                bottom=top+view.getMeasuredHeight();

                view.layout(0,top,view.getMeasuredWidth(),bottom);
                mViewList.add(view);
                Log.d(TAG, "add indeax"+i);

                top=bottom;
            }


        }
    }

    public View obtionAndAddView(int postion){
        int itemType = mAdapter.getItemViewType(postion);
        // 通过指定 position 位置的View类型去回收池查找View，存在则复用，不存在则调用 onCreateViewHolder 创建
        View view = mRecycler.get(itemType);
        if(view==null){
            view = mAdapter.onCreateViewHolder(this,itemType);

        }


        Log.d(TAG, "obtionAndAddView: child height"+view.getLayoutParams().height);

        //layoutParams 的宽高包括 match——parent等如果-1 放在无符号上面大值，如果图片还会oom
        if (view.getLayoutParams().width== LinearLayout.LayoutParams.MATCH_PARENT){
            view.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(),MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(view.getLayoutParams().height,MeasureSpec.EXACTLY));
        }else{
            view.measure(MeasureSpec.makeMeasureSpec(view.getLayoutParams().width,MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(view.getLayoutParams().height,MeasureSpec.EXACTLY));
        }


        mAdapter.onBinderViewHolder(postion,view,this);
        view.setTag(R.id.tag_type,itemType);

        //index值主要是linnearlayout等做位置插入
        addView(view);


        return view;
    }
    //--------------------------------------touch

    /**
     * 滑动超过范围才拦截
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;
        Log.d(TAG, "onTouchEvent: scrolerr"+ev.getAction());

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mCurrentY=ev.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                //gety 相对自身左上角 getrawy相对于屏幕的
                float y2=Math.abs(mCurrentY - (int) ev.getRawY());
                Log.d(TAG, "onTouchEvent: scrolerr="+y2);

                if(y2>mMinSlop){
                    intercept =true;
                }
                break;
        }

//        return true; 即使拦截但是没有touchdown没有被outouchevent消费（return true）仍然获取不到后面事件
        return intercept;
    }


    /**
     * 计算需要scroll的距离
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_MOVE){
            float cur=event.getRawY();

            //这个值是相对于scroller的  正值内容向上
            float diff=mCurrentY-cur;
            mCurrentY=cur;

            scrollBy(0,(int)diff);
        }

        Log.d(TAG, "onTouchEvent: scrolret ");
//        默认返回的false 后面所有事件收不到了，返回true 子view还能收到click
//        return super.onTouchEvent(event);
        return true;

    }

    @Override
    public void scrollBy(int x, int y) {
        //记录第一个可见顶点距离 recyclerview的左上角的距离
        mScrollY += y;




        // 上滑正  下滑负  边界值
        Log.d(TAG, "scrollBy: ele   scroll"+mScrollY+"y="+y);



        if(mScrollY>0){
            //上滑 把超过的全部删除掉
            Log.d(TAG, "scrollBy: ele add ele");

            while(mScrollY > mViewList.get(0).getMeasuredHeight()){
                removeView(mViewList.remove(0));
                mScrollY -= mViewList.get(0).getMeasuredHeight();
                firstRow++;
                Log.d(TAG, "scrollBy: ele add ele1");

            }

            // 上滑 添加
            while (getFillHeight() < getHeight()) {
                int addLast = firstRow + mViewList.size();
                View view = obtionAndAddView(addLast);
                Log.d(TAG, "scrollBy: ele add ele2");



                mViewList.add(mViewList.size(), view);

            }

        } else if (mScrollY < 0) {




            // 下滑加载
            while (mScrollY < 0) {
                int firstAddRow = firstRow - 1;
                View view = obtionAndAddView(firstAddRow);
                mViewList.add(0, view);
                firstRow--;
                Log.d(TAG, "scrollBy: ele down1 ");

                mScrollY += view.getMeasuredHeight();
            }

            // 下滑移除
            while (getFillHeight()- mViewList.get(mViewList.size()-1).getHeight()>= getHeight()) {
                removeView(mViewList.remove(mViewList.size() - 1));
                Log.d(TAG, "scrollBy: ele down2 ");

            }


        }

        repositionViews();

    }

    private void repositionViews() {
        int left, top, right, bottom, i;
        top = -mScrollY;
        i = 0;
        for (View view : mViewList) {
            bottom = top + mViewList.get(i++).getMeasuredHeight();
            view.layout(0, top, getWidth(), bottom);
            top = bottom;
        }
    }


    /**
     * @return 数据的高度 -scrollY
     */
    private int getFillHeight() {
        int sum=0;
        for(int  i=0;i<mViewList.size();i++){
            sum+=mViewList.get(i).getMeasuredHeight();
        }
        return sum - mScrollY;
    }


    @Override
    public void removeView(View view) {
        super.removeView(view);
        int key = (int) view.getTag(R.id.tag_type);
        mRecycler.put(key,view);
    }



    // -------------------------------------------------------------------------

    /**
     * 官方用的viewholder, Adapter<VH extends ViewHolder> 因为Recyler管理的是ViewHolder，
     * viewholder的目的是不去findviewByid 这里使用view即可，外面去holder
     *
     */
    interface Adapter {

        View onCreateViewHolder(ViewGroup parent,int viewType);

        void onBinderViewHolder(int position, View convertView, ViewGroup parent);

        // Item的类型
        int getItemViewType(int row);

        // Item的类型数量
        int getViewTypeCount();

        int getCount();


    }

    /**
     *步骤3
     * 就要两个 cache和 pool--简单点用一个
     * 官方的pool其实就是sparse array存的
     *
      */
    public class Recycler{

//       官方 final ArrayList<ViewHolder> mCachedViews = new ArrayList<ViewHolder>();

        HashMap<Integer,ArrayList<View> > poolListMap;
        SparseArray<ArrayList<View>> poolListArray=new SparseArray<>();


        Recycler(){


        }

        public View get(int imageType){
            ArrayList<View> views=poolListArray.get(imageType);
            if(views==null||views.size()==0){
                return null;
            }else{
                View view=views.get(0);
                views.remove(0);
                return view;

            }

        }

        public void put(int imageType,View view){

            ArrayList<View> views=poolListArray.get(imageType);
            if(views==null||views.size()==0){
                views=new ArrayList<>();
                views.add(view);
                poolListArray.put(imageType,views);
            }else{
                views.add(view);

            }

        }





    }

}
