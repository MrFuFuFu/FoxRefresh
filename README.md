FoxRefresh
=====================


## 描述

小狐狸下拉刷新，如图，好吧，又一个下拉刷新，`PullRefreshBaseView`继承自`FrameLayout`里面放了`AbsListView`，上层`PullRefreshProgressListView`继承自`RelativeLayout`里面放了 [PullRefreshBaseView](https://github.com/MrFuFuFu/MrFuPullToRefresh) 和 [SmoothProgressBar](https://github.com/castorflex/SmoothProgressBar)，这么做的目的是为了是让`SmoothProgressBar` 能在最顶部显示出来。 上拉加载中使用的 [CircularProgress](https://github.com/castorflex/SmoothProgressBar) 同样使用的是 [castorflex](https://github.com/castorflex) 的 [SmoothProgressBar](https://github.com/castorflex/SmoothProgressBar) 的开源项目，oh，开源真是太方便了。I love open source code.

## 如何使用

>虽然 `PullRefreshProgressListView` 不是一个 `ListView`, 但是这里你可以定义所有关于 `ListView` 的方法，因为 我已经将这些属性都赋给了 `PullRefreshProgressListView` 声明的 `ListView`。

```xml
<mrfu.foxrefresh.lib.PullRefreshProgressListView
    android:id="@+id/pull_refresh_progress_baseview"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:cacheColorHint="#00000000"
    android:divider="@android:color/transparent"
    android:dividerHeight="5dp"
    android:fadingEdge="none"
    android:fastScrollEnabled="false"
    android:listSelector="@android:color/transparent"
    android:overScrollMode="never"
    android:layout_below="@+id/toolbar" />
```

>`PullRefreshProgressListView` 不是一个 `ListView`， 在设置 adapter 的时候，你需要调用 `pull_refresh_progress_baseview.getListView()`; 方法取出 `ListView`

```java
pull_refresh_progress_baseview = (PullRefreshProgressListView)findViewById(R.id.pull_refresh_progress_baseview);
pull_refresh_progress_baseview.setPullRefreshListener(this);
ListView listView = pull_refresh_progress_baseview.getListView();
adapter = new ListViewAdapter();
initData();
listView.setAdapter(adapter);
pull_refresh_progress_baseview.setRefreshing();
```

>别忘了实现如下接口

```java
public class MainActivity extends Activity implements PullRefreshListener{
```

>* `onPullDownRefresh` 为下拉的时候，要做的事情，你可以在这个时候去请求网络数据。

>* `onPullUpRefresh` 为上拉加载的时候，要做的事情，这个时候你可以去请求下一页的数据，如果你不需要上拉加载，你可以调用 `setEnablePullUpRefresh(false)` 方法关闭上拉加载。

```java
@Override
public void onPullDownRefresh() {
    pull_refresh_progress_baseview.postDelayed(new Runnable() {
        @Override
        public void run() {
            initData();
            pull_refresh_progress_baseview.reset();
        }
    }, 1500);
}
@Override
public void onPullUpRefresh() {
    pull_refresh_progress_baseview.postDelayed(new Runnable() {
        @Override
        public void run() {
            initData();
            pull_refresh_progress_baseview.reset();
        }
    }, 1000);
}
```

***
***
我是传说中的分割线
***
***


> 好吧，我最近在学英语，就让我用英语来描述一遍吧...也许，有很多语法错误

> Ok, I recently learned English, let me use English to describe it again. Maybe, there will be a lot of grammatical errors.

## Describe

Fox Pull to refresh，As the gif shows, `PullRefreshBaseView` inherit form `FrameLayout`. Inside has a `AbsListView`, `PullRefreshProgressListView` inherit form `RelativeLayout`. Inside has [PullRefreshBaseView](https://github.com/MrFuFuFu/MrFuPullToRefresh) and [SmoothProgressBar](https://github.com/castorflex/SmoothProgressBar). The goal is to let `SmoothProgressBar ` can be displayed at the top. up to load is use [CircularProgress](https://github.com/castorflex/SmoothProgressBar). it is also use [castorflex'](https://github.com/castorflex) [SmoothProgressBar](https://github.com/castorflex/SmoothProgressBar), oh, I love open source code.


## How to use?

>Althouh `PullRefreshProgressListView` is not a `ListView`, but in the xml you can define all about `ListView` attributes. Because I had use those attributes assignment to the `PullRefreshProgressListView` of `ListView`.

```xml
<mrfu.foxrefresh.lib.PullRefreshProgressListView
    android:id="@+id/pull_refresh_progress_baseview"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:cacheColorHint="#00000000"
    android:divider="@android:color/transparent"
    android:dividerHeight="5dp"
    android:fadingEdge="none"
    android:fastScrollEnabled="false"
    android:listSelector="@android:color/transparent"
    android:overScrollMode="never"
    android:layout_below="@+id/toolbar" />
```

>PullRefreshProgressListView is not a ListView, so when you are setAdapter, you should use `pull_refresh_progress_baseview.getListView()` to get the `ListView`.


```java
pull_refresh_progress_baseview = (PullRefreshProgressListView)findViewById(R.id.pull_refresh_progress_baseview);
pull_refresh_progress_baseview.setPullRefreshListener(this);
ListView listView = pull_refresh_progress_baseview.getListView();
adapter = new ListViewAdapter();
initData();
listView.setAdapter(adapter);
pull_refresh_progress_baseview.setRefreshing();
```

>Don't forget implements PullRefreshListener.

```java
public class MainActivity extends Activity implements PullRefreshListener{
```

>* `onPullDownRefresh` is pull down refresh. You can go to request data fomr the network at this time.

>* `onPullUpRefresh` is up to load more data, You can go to request next page data form network at this time. if you don't need up to load more data, you can use `setEnablePullUpRefresh(false)` method to  close it.

```java
@Override
public void onPullDownRefresh() {
    pull_refresh_progress_baseview.postDelayed(new Runnable() {
        @Override
        public void run() {
            initData();
            pull_refresh_progress_baseview.reset();
        }
    }, 1500);
}
@Override
public void onPullUpRefresh() {
    pull_refresh_progress_baseview.postDelayed(new Runnable() {
        @Override
        public void run() {
            initData();
            pull_refresh_progress_baseview.reset();
        }
    }, 1000);
}
```



## Screenshot

![FoxRefresh_screen.gif](img/FoxRefresh_screen.gif)

## More about me

* [MrFu-傅圆的个人博客](http://mrfu.me/)

License
============

    Copyright 2015 MrFu

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.