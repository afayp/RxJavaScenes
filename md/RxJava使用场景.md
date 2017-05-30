# RxJava使用场景

## Android使用场景

### Scheduler线程切换
```java
Observable.just(1, 2, 3, 4)  
        .subscribeOn(Schedulers.io()) //后台取数据
        .observeOn(AndroidSchedulers.mainThread())//主线程处理
        .subscribe(new Action1<Integer>() {  
	        @Override  
	        public void call(Integer number) {  
	            Log.d(tag, "number:" + number);  
	        }  
    	});  
```

### debounce做textSearch
```java
EditText et_search = (EditText) findViewById(R.id.et_search);
final TextView tv_result = (TextView) findViewById(R.id.tv_result);
RxTextView.textChangeEvents(et_search)
        .debounce(1, TimeUnit.SECONDS)
        .filter(new Func1<TextViewTextChangeEvent, Boolean>() {
            @Override
            public Boolean call(TextViewTextChangeEvent textViewTextChangeEvent) {
                return !TextUtils.isEmpty(textViewTextChangeEvent.text().toString()); // 空字符不发射
            }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<TextViewTextChangeEvent>() {
            @Override
            public void onCompleted() {
                tv_result.setText(tv_result.getText().toString() + "\n" +"搜索完毕");
            }

            @Override
            public void onError(Throwable e) {
                tv_result.setText(tv_result.getText().toString() + "\n" +"搜索出错");
            }

            @Override
            public void onNext(TextViewTextChangeEvent textViewTextChangeEvent) {
                tv_result.setText(tv_result.getText().toString() + "\n" +"搜索"+textViewTextChangeEvent.text());
            }
        });
```

### throttleFirst防止重复点击
```java
Button btn_click = (Button) findViewById(R.id.btn_click);
RxView.clicks(btn_click)
        .throttleFirst(5, TimeUnit.SECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<Void>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Void aVoid) {
                count++;
                Toast.makeText(RepeatClickActivity.this,"成功点击次数"+count,Toast.LENGTH_SHORT).show();
            }
        });
```

### CombineLatest合并多个Observable的最新数据
> 场景：表单的验证，有很多选项，必须全部填写正确才能进入下一步。

```java
Observable<CharSequence> usernameObservable = RxTextView.textChanges(et_username);
Observable<CharSequence> passwordObservable = RxTextView.textChanges(et_password);
Observable.combineLatest(usernameObservable, passwordObservable, new Func2<CharSequence, CharSequence, Boolean>() {
    @Override
    public Boolean call(CharSequence username, CharSequence password) {
        return !TextUtils.isEmpty(username.toString()) && !TextUtils.isEmpty(password.toString());
    }
	}).subscribe(new Observer<Boolean>() {
	    @Override
	    public void onCompleted() {
	
	    }
	
	    @Override
	    public void onError(Throwable e) {
	
	    }
	
	    @Override
	    public void onNext(Boolean aBoolean) {
	        btn_login.setVisibility(aBoolean ? View.VISIBLE : View.INVISIBLE);
	    }
	});
```
### concat按顺序从多个数据源加载数据
> 场景： 某个页面先显示本地历史的数据，网络获取到最新数据后再显示最新数据，防止中间出现空白。

```java
Observable<String> fastLocalData = Observable.just("来自本地缓存").delay(1, TimeUnit.SECONDS);
Observable<String> slowNetData = Observable.just("来自网络获取").delay(3, TimeUnit.SECONDS);
Observable.concat(fastLocalData,slowNetData);
```

### amb取响应最快的数据源
> 场景： 有两个接口都可以获取想要的数据，取响应最快的那个，慢的那个被丢弃。基本等价于concat+first


```java
Observable<String> first = Observable.timer(1, TimeUnit.SECONDS).map(new Func1<Long, String>() {
    @Override
    public String call(Long aLong) {
        return "first";
    }
});
Observable<String> second = Observable.timer(1, TimeUnit.SECONDS).map(new Func1<Long, String>() {
    @Override
    public String call(Long aLong) {
        return "first";
    }
});
Observable.amb(first,second);
```



### timer延时操作
```java
Observable.timer(3, TimeUnit.SECONDS)
            .subscribe(new Action1<Long>() {
                @Override
                public void call(Long aLong) {
                    // 3秒延时后执行的操作
                }
            });
```

### interval周期性操作,每隔xx秒后执行yy操作
```java
Observable.interval(2, TimeUnit.SECONDS)  
         .subscribe(new Observer<Long>() {  
             @Override  
             public void onCompleted() {  
                log.d ("completed");  
             }  
  
             @Override  
             public void onError(Throwable e) {  
                log.e("error");  
             }  
  
             @Override  
             public void onNext(Long number) {  
                log.d ("hello world");  
             }  
         });  

```

### zip等待多个接口取完数据，再操作
```java
Observable<String> observable1 = DemoUtils.createObservable1().subscribeOn(Schedulers.newThread());
Observable<String> observable2 = DemoUtils.createObservable2().subscribeOn(Schedulers.newThread());

Observable.zip(observable1, observable2)
        .subscribeOn(Schedulers.newThread())
        .subscribe(System.out::println);

```

