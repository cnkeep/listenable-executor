listenable-executor
---------------------------


## 介绍
listenable-executor是一个支持异步回调事件的线程池模型，为解决jdk原生线程池不支持事件机制，获取结果需要阻塞等待的痛点。

> listenable-executor具有如下特点：  

* 支持异步回调事件，这也是它最大的特性  
* 兼容jdk原生的接口调用，使用新的特性只需调用新增的相关接口即可  
* 无任何第三方依赖库  
* 可自定义线程池生成的线程名称

## 快速开始
### 环境介绍
* `jdk>=1.8` 
* `git`  
* `maven`
### 下载源码
通过git拉取源码到本地
```text
git clone https://github.com/cnkeep/listenable-executor.git
```
### 编译打包
通过maven工具将该项目打包并安装到本地仓库
```text
cd listenable-executor
mvn clean -Dmaven.test.skip=true package install
```

### 集成
#### 添加依赖
新建maven工程，在*pom.xml*中增加*listenable-executor*依赖：  
```text
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <properties>
        <jdk.version>1.8</jdk.version>
    </properties>
    
    <dependencies>
            <!-- 添加listenable-executor依赖  -->
            <dependency>
                <groupId>github.com.cnkeep</groupId>
                <artifactId>listenable-executor</artifactId>
                <version>1.0-SNAPSHOT</version>
            </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>${jdk.version}</source>
                    <target>${jdk.version}</target>
                </configuration>
            </plugin>
    </build>
</project>
```
#### 构建线程池
```text
ListenableExecutorService executor = new ListenableThreadPoolExecutor(
                                                1,
                                                1,
                                                0, TimeUnit.SECONDS,
                                                new LinkedBlockingDeque<>(),
                                                new ThreadFactoryBuilder().nameFormat("listable %d").build(),
                                                new ThreadPoolExecutor.AbortPolicy());
```
#### 使用新提供的接口提交任务和回调事件
```text
    Runnable successRunnable = () -> {
        System.out.println("run");
    };

    FutureCallback<String> callBack = new FutureCallback<String>() {
        //执行成功时回调
        public void success(Callable<String> task, String result) {
             System.out.println(task+" callback success with result:"+result);
        }
        //执行失败时回调
        public void fail(Callable<String> task, Throwable cause) {
            System.out.println(task+" callback failed cause by:"+cause);
        }
    };
    
    //提交任务和回调事件
    executor.submit(successRunnable,callBack);
```
#### 关闭线程池
```text
 executor.shutdown();
```
至此项目集成完成！

## 原理介绍
### 原生线程池的痛点
当我们使用使用线程池的异步操作时，如果需要获取任务执行结果，我们调用`Future.get`方法会阻塞，知道任务执行完毕，但这并不是完全的异步
机制啊，我们想要的异步是执行异步，之后的结果也是异步的，我们可以直接通过注册事件的形式，告诉线程池，执行成功我们希望做什么，执行失败我们
希望做什么。  

正是在以上的基础上，才诞生了这个项目，这个项目正是通过改写jdk原生的线程池流程，提供了异步的事件回调处理。   

### 原理
我们知道所有的任务最终都会转化为`FutureTask`提交到线程池去执行，而所有的任务执行完毕(无论成功，失败，取消)都会调用`done`方法，
那么`done`方法就成为了我们的切入点，只要在此方法中处理我们的回调事件即可，具体实现请查阅`ListenableFutureTask.done()`方法：  
```text
 /**
     * 扩展点
     */
    @Override
    protected void done() {
        Throwable cause = null;
        try {
            V result = get();
            if (null != this.successCallBack) {
                successCallBack.success(target, result);
            }
            return;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            cause = e.getCause();
            if (null == cause) {
                cause = e;
            }
        } catch (Throwable throwable) {
            cause = throwable;
        }
        if (null != this.failCallBack) {
            failCallBack.fail(target, cause);
        }
    }
```

## 其他问题

* 因个人水平有限，代码难免存在bug, 大家可以在issues中提出，亦可emailTo:zhangleili924@gmail.com, 我会及时处理。 
* 本项目属于开源代码，大家可任意下载修改。
