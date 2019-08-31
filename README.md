# WatFramework
一款集LOG、IOC、AOP、MVC、ORM的快速原型开发工具
## 自定义日志类
- 实现功能：不需要在每一个类中通过LoggerFactory.getLogger，而是直接类似控制台输出的形式随时输出日志。

- 实现原理：自定义工具类，工具类中设置相应的静态方法，这些方法可分别被调用，然后通过遍历堆栈信息获取调用该日志类的名字，从而打印日志。

- 实现难点：需要输出当前类的信息，也就是需要在LogUtil中获得被调用的类的classname。

- 解决方案：通过	Thread.currentThread().getStackTrace(); 类获得当前线程堆栈转储的堆栈跟踪元素数组，然后遍历该数组，先找到LogUtil日志类，然后按顺序找到之后第一个非日志类的堆栈，这就是最原始被调用的方法，然后输出log信息。

## 自定义线程池

- 实现原理：
new ThreadPoolExecutor(2, 4, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(10));
  
- 参数解释：（核心线程数，最大线程数，线程存活时间，时间单位，线程队列）

## JDBM缓存工具类

- 实现功能：CacheUtil.insert("iocCache", "2", "1");
参数解释：（缓存名，key，value）

- 实现原理：
1.根据数据库名，创建RecordManager实例对象，类似建一个库
2.通过RecordManager实例对象构建一个BTree对象，类似建一张表
3.再通过BTree对象添加数据

## AOP
- 实现思路：定义两个注解，Aspect@Target(ElementType.TYPE)和PointCut@Target(ElementType.METHOD)，供用户使用。系统启动时，根据用户指定的目录，先扫面找出切面的类，再从该类遍历找出所有的切点，再从注解中提取value值，将其拆分为被代理的类名和被代理的方法名，再更具切点创建出被代理的类的对象，根据切面类创建代理者，再设置代理的方法，最后创建出实际的对象，将其存入AOP容器中，等待被使用。

## IOC

- 实现功能：将所有的类的实例化过程托管给平台来实现，用户直接调用即可

- 实现原理：定义四个注解，Autowired@Target(ElementType.FIELD)，Controller@Target(ElementType.TYPE)，Service@Target(ElementType.TYPE)，Repository@Target({ ElementType.METHOD, ElementType.TYPE })供用户使用。定义一个缓存，将缓存作为ioc容器，系统启动后，依次扫描所有的类，将所有标记了Controller和Service等注解的类创建实例，然后以小写头字母的类名为key，实例为value，存入缓存。等到为所有bean创建完实例后，再进行一次遍历，找出所有标记了Autowired方法的成员变量，先setAccessible(true),获取权限，然后通过set的方法将bean注入到类中，若该Autowired注解有value值，则为按值注入，若注解没有值，则默认按类型注入，最后将对象更行到缓存中。

## MVC

- 实现功能：所有请求通过一个Servlet来转发到各个控制器。

- 实现原理：定义两种注解，RequestMapping@Target({ ElementType.METHOD, ElementType.TYPE })，RequestParam@Target(ElementType.PARAMETER)，供用户使用。定义一个缓存，将缓存作为method容器。系统启动后，该容器中以类+方法名为key，method实例为value。这个过程可以在创建ioc容器时同完成。再定义一个中心控制器，所有的请求都会集中到该控制器中，然后该控制器通过解析出请求的url和参数，直接调用所请求的方法。

## ORM

- 实现功能：通过注解的方式直接在接口上写sql语句

- 实现原理：定义五种注解，Insert，Delete，Update，Select和Param，供用户使用。用户可以通过动态代理获取对象，通过Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},new MyInvocationHandlerMbatis(clazz));实现动态代理功能，实现InvocationHandler接口，然后在其中的invoke方法中判断是那一种注解，然后根据注解value和param值动态拼接sql语句，然后通过数据库连接池自动获取连接并执行sql语句，返回查询的对象。
