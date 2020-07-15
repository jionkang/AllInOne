//
// Created by Xie Jiantao on 2020-07-15.
//

#ifndef ALLINONE_STD_HANDLER_H
#define ALLINONE_STD_HANDLER_H

#include <iostream>
//#include <cstring> //strlen 不需要这个
#include <vector>
#include <iterator>//ostream_iterator
#include <string>
#include <list>
#include <map>
#include <unordered_map>
#include <queue>
#include <algorithm>
#include <stack>
#include <android/log.h>
#include <jni.h>


#define mout(t) cout<<t<<endl
//s#define  mout(...)  __android_log_print(ANDROID_LOG_ERROR,"std-native",__VA_ARGS__)

using namespace std;

class Std_handler {

/**
标准库的常用操作
增加删除查询（随机，非随机）改 插入
*/
public:
/*
*char* 不能修改  增，删除，修改都不行，所以除非你用数组！！！！
*cra数组初始化方式
*/

    void charFunc() {
        mout("==char test==");
//	const char* crp="1234";
        char *crp = "1234";  //not allow conversion from string literal to 'char *'
        char cra[] = "5678";//array 或者数组大于
        mout(strlen(cra));  //strlen
//	crp[0]='a';    //error！！！ const char* cant modify --本身是const的
//	mout(crp);
        cra[0] = 'b';

    }

/**
增删改查
*/
    void stringFunc() {
        mout("==string test==");

        string str = "1234";
        str[0] = 'a';
        str.push_back('5');
        mout(str);
        str.erase(1, 1);  //删除positon即可
        mout(str);
        mout(str.find("34"));
        mout(str[2]);

    }


    void vectorFunc() {
        mout("==vector test==");
        vector<int> vc{1, 2, 3, 4};
        vc.push_back(100);//add
        vc[0] = 101;//update
        cout << vc[0] << endl;//101
        vc.pop_back(); //remove 100
        vc.erase(vc.begin() + 1);
        ostream_iterator<int> out_iter(cout, " ");//os_iter使用方法
        copy(vc.begin(), vc.end(), out_iter);
        cout << endl;
        //随机插入耗时
        vc.insert(vc.begin(), 8);
        copy(vc.begin(), vc.end(), out_iter);
        cout << endl;

    }

/**
*list 用处很少，没有next等指针暴露，用这个写单链表逆序除非操作迭代器  你有要处理beginend等，不如自己定义list
*/
    void listTest() {
        mout("==list test==");
//	List<int> list;//c++ stl没有大写开头的
        list<int> lt{1, 2, 3, 4};
        lt.push_back(100);
        lt.erase(lt.begin());
        ostream_iterator<int> out_iter(cout, " ");//os_iter使用方法
        copy(lt.begin(), lt.end(), out_iter);
        cout << endl;
        lt.insert(lt.begin(), 101);
        //for 迭代器查询 --真心难用

    }

/*
*好像map就够了吧 不需要unordered吧
*/
    void mapFunc() {
        mout("==map test==");
        map<int, int> mp;
        mp[0] = 1;
        mp[15] = 2;
        cout << mp[15] << endl;//数组操作要count
        mp[15] = 5;
        cout << mp[15] << endl;//数组操作要count
        mp.erase(15);  //直接对key erase就好
        cout << mp[15] << endl;//数组操作要count
    }

/*
*注意头文件就是<queue> 但是它的实现是堆
*priority_queue<int> q;
*priority_queue<int,vector<int>,less<int> >;
*less 表示数字大的优先级高，而 greater 表示数字小的优先级高。---从前往后看怎么会记错呢
*默认less 也就是大的在前面
* 数据结构为啥会忘  队列先进先出！！又不是双端队列  所以都是 push  pop 和top，peek是java的
*/

    void funcPQueue() {
        mout("==queue priority_queue test==");

//	priority_queue<int,greater<int>> pq;  //错误的 有比较 必须声明容器，要不比较器不知道你用什么实现的
        priority_queue<int, vector<int>, greater<int>> pq;

//	pq.push_back(100);  //  error！！！priority_queue stack queue 没有push_back   ||但是queue就有
        pq.push(100);
        pq.push(10);
        pq.push(101);

        cout << pq.top() << endl; //10的确就是最小的
        pq.pop();


        mout("==queue stack test==");

        queue<int> que;
        que.push(1);    //
        cout << que.front() << endl; //queues是front
        que.pop();

        stack<int> st;
        st.push(1);
        cout << st.top() << endl;
        st.pop();


    }


/*

==char test==
4
==string test==
a2345
a345
1
4
==vector test==
101
101 3 4
8 101 3 4
==list test==
2 3 4 100
==map test==
2
5
0
==queue priority_queue test==
10
==queue stack test==
1
1
*/

    int main() {
        charFunc();
        stringFunc();
        vectorFunc();
        listTest();
        mapFunc();
        funcPQueue();
        return 0;

    }

};


#endif //ALLINONE_STD_HANDLER_H
