关于properties文件中占位符的使用
上面我们知道了它支持强大的占位符，其实它还支持取值一些内置的变量。比如环境变量和环境属性。官方文档见：Variable Interpolation

个人建议以官方说明为准，此处大概的总结一下。
如果支持Ant或者Maven就知道，他们都是是支持读取环境变量的。
显然此处强大的它必须是也赋予了properties配置文件这个基本的功能特性。

它内置三个属性前缀如下：

- sys：对应我们熟悉的systemProperties，不解释
- env：对应我们熟悉的environmentProperties，不解释
- const：The const prefix indicates that a variable is to be interpreted as a constant member field of a class (i.e. a field with the static final modifiers). The name of the variable must be of the form <full qualified class name>.<field name>. The specified class will be loaded and the value of this field will be obtained.

关于systemProperties和environmentProperties可参考博文：【小家Java】Java环境变量（Env）和系统属性（Property）详解—工具文章

使用案例：
```shell

user.file = ${sys:user.home}/settings.xml
java.home = ${env:JAVA_HOME}

# 可以理解为直接引用某个类的静态常量~ 还是蛮强大的
action.key = ${const:java.awt.event.KeyEvent.VK_CANCEL}

```
占位符使用注意事项：

1. 如果某个变量无法解析，例如，因为名称无效或使用了未知前缀，则不会替换该变量，而是按原样输出，包括美元符号和大括号。（so，不可能输出null）
2. 对变量的引用可以嵌套；变量可以引用一个变量，而该变量又可以引用其他变量等等。
3. 检测到循环引用。在这种情况下，直接报错：Infinite loop in property interpolation
4. 变量插值发生在从配置中查询属性时，而不是在创建时（所以我们的使用变量可以达到很好的动态刷新属性值的效果）。因此此机制是动态的：一个属性上的更改会影响引用第一个属性的另一个属性的值。
5. 只允许同一个文件的属性值引用，不能垮文件。另外，同一个文件内属性值引用，文件内可以无序（上面一行也可能引用下面的key，和顺序无关）

### 关于properties文件的写

以上所有内容都是讲的读取properties文件，确实我们绝大部分情况下都只需要读取。
但是这里稍微介绍一下对它的写：向里面设置一个k-v，然后最终写进文件里（持久化）

```java
// 这里单独set，只是保存在了内存了，并还没有持久化的  需要注意~~~~
Configuration config = configs.properties("my.properties");
config.setProperty("dev", "dev");
```
关于它的具体写入逻辑以及中文乱码的处理，这里篇幅限制暂时略过，有兴趣的可以自己稍微研究一下即可。（因为写入确实用得太少了，时间原因所以这里就暂时忽略~）

处理XML文件：ExpressionEngine
相比properties文件格式 xml要复杂多了。虽然现在SpringBoot摒弃了xml配置，但是某些时候使用xml还是更加的适合些，比如日志框架（logback）的配置文件等。
xml的复杂在于：它不仅仅可以表示k-v，并且每个节点都可以有attribute以及子节点。所以对于xml格式的配置文件，需要一套规则来处理（比如你的attr叫什么名字，这个都是框架本身并不能知道的）。

commons configuration把这个称作ExpressionEngine引擎。它给我们提供了一套默认的规则引擎，但是这个显然你也是可以自定义的，也可以使用xpath的标准。

下面的代码分别示例如何使用：默认规则、xpath、自定义规则来访问Xml配置文件中的节点.
首先在类路径放置一个文件：my.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <token>
        <device>
            <validate>true</validate>
        </device>
        <person>
            <validate>true</validate>
            <expire description="人员令牌失效时间(分钟) ">60</expire>
        </person>
    </token>
</configuration>
```
以上：此处的description是节点属性(attribute)

默认规则引擎
使用提供给我们的默认规则来处理：

```java
    public static void main(String[] args) throws ConfigurationException {
        Configurations configs = new Configurations();

        XMLConfiguration config = configs.xml("my.xml");

        // 使用默认的符号定义创建一个表达式引擎
        DefaultExpressionEngine engine = new DefaultExpressionEngine(DefaultExpressionEngineSymbols.DEFAULT_SYMBOLS);
        // 指定表达式引擎
        config.setExpressionEngine(engine);
        System.out.println(config.getBoolean("token.device.validate"));
        System.out.println(config.getInt("token.person.expire"));

        // 此处就是它的默认规则 [@xxx]表示取对应attr的值~  更多默认定义请参考DEFAULT_SYMBOLS常量
        System.out.println(config.getString("token.person.expire[@description]"));
    }

```
输出结果为：

true
60
人员令牌失效时间(分钟)


结果正确并且还没有乱码（不用处理乱码也是xml的一大优势）。

### xpath规则引擎

要使用xpath引擎，请先导入jar：

```xml
<!-- https://mvnrepository.com/artifact/commons-jxpath/commons-jxpath -->
<dependency>
    <groupId>commons-jxpath</groupId>
    <artifactId>commons-jxpath</artifactId>
    <version>1.3</version>
</dependency>
```
示例代码：
```java

    public static void main(String[] args) throws ConfigurationException {
        Configurations configs = new Configurations();


        XMLConfiguration config = configs.xml("my.xml");

        // 使用 XPath表达式引擎
        XPathExpressionEngine xpathEngine = new XPathExpressionEngine();
        config.setExpressionEngine(xpathEngine);
        System.out.println(config.getBoolean("token/device/validate"));
        System.out.println(config.getInt("token/person/expire"));

        // 请注意这里路径分隔符和attribute标签与上面使用DefaultExpressionEngine是不同的
        System.out.println(config.getString("token/person/expire/@description"));
    }
```

使用xpath的好处：xpath是一种通用的标准，大家遵守起来比较容易。
缺点：需要额外导入jar。
但总体来说大多数情况下，我觉得选择xpath还是一种不错的选择~~

解析xml一直都是一件非常头疼的事，现在有了commons-configuration简直不要太方便有木有~


ExpressionEngine接口内置的两个实现：一个DefaultExpressionEngine，另外一个就是XPathExpressionEngine了


自定义规则引擎
如果上面两种表达式引擎都不合你的意，比如你很任性就是不喜欢/@description和[@description]这样别扭的attr标签。你可以用自定义的符号规则生成一个ExpressionEngine表达式引擎对象。

此处举例：直接使用@作为属性符号取值：

    public static void main(String[] args) throws ConfigurationException {
        Configurations configs = new Configurations();
        DefaultExpressionEngineSymbols symbols = new DefaultExpressionEngineSymbols.Builder(
                DefaultExpressionEngineSymbols.DEFAULT_SYMBOLS)
                // 指定属性分隔符(路径分隔符)
                .setPropertyDelimiter(".")
                .setIndexStart("{")
                .setIndexEnd("}")
                // 指定@开头就是attribute标志
                .setAttributeStart("@")
                // attribute结尾符为null  也就是木有结尾符
                .setAttributeEnd(null)
                // A Backslash is used for escaping property delimiters
                .setEscapedDelimiter("\\/")
                .create();
        // 用自定义的符号DefaultExpressionEngineSymbols对象创建一个表达式引擎
        // 此处还是使用的DefaultExpressionEngine~~~这个对象
        DefaultExpressionEngine engine = new DefaultExpressionEngine(symbols);
        Parameters params = new Parameters();


        FileBasedConfigurationBuilder<XMLConfiguration> builder = new FileBasedConfigurationBuilder<>(XMLConfiguration.class)
                .configure(params.xml()
                        .setFileName("my.xml")
                        // ~~~~~~~~~~~~~~~~~~~~~~~~~~使用自定义的表达式引擎~~~~~~~~~~~~~~~~~~~~~~~~~~
                        .setExpressionEngine(engine));


        // 使用builder生成配置~  而不用使用Configurations帮忙了~
        XMLConfiguration config = builder.getConfiguration();
        System.out.println(config.getBoolean("token.device.validate"));
        System.out.println(config.getInt("token.person.expire"));

        // 看此处 符号换成了我们自定义的~
        System.out.println(config.getProperty("token.person.expire@description"));
    }
复制
一般情况下个人不太建议自定义，毕竟xpath形成一个标准也不容易，直接使用反倒更加省心，建议遵守。

处理Yaml文件
在现在Yaml越来越流行，也确实它比properties文件更加的直观和简洁，所以解析它成为一个基础能力。

在类路径下准备一个my.yaml：

spring:
application:
name: mydemoshow::${person.name}

person:
name: fsx
age: 10

示例代码如下：

    public static void main(String[] args) throws Exception {
        YAMLConfiguration config = new YAMLConfiguration();
        config.read(new ClassPathResource("my.yaml").getInputStream());

        Iterator<String> keys = config.getKeys();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = config.getString(key);
            System.out.println(key + " = " + value);
        }
    }
复制
输出：

spring.application.name = mydemoshow::fsx
person.name = fsx
person.age = 10
复制
结果正确，并且它也能使用和properties一样的占位符~

文件扫描策略
从上面的例子可以看到，我们的文件放在classpath下，就能够被加载进来。那么这里面到底有怎么样的规则呢？

从2.x版本开始，对于文件扫描策略，用接口FileLocationStrategy来实现，该接口只有一个单一的方法locate(),

// @since 2.0
public interface FileLocationStrategy {
URL locate(FileSystem fileSystem, FileLocator locator);
}
复制
它内置的实现如下：



ClasspathLocationStrategy：从classpath下去加载文件**（常用）**
AbsoluteNameLocationStrategy：绝对路径。所以直接使用new File(locator.getFileName())去加载
HomeDirectoryLocationStrategy：从user.home里去查找
BasePathLocationStrategy：使用basePath+fileName的方式new File()
FileSystemLocationStrategy：使用文件系统定位。比如windows是带盘符的
ProvidedURLLocationStrategy：直接是URL的方式。所以它也可以存在于网络上~
CombinedLocationStrategy：真正使用的。它是一个聚合，这些实现类可以构成一个扫描链来进行按照其顺序进行组合扫描，之前讲过很多类似的设计模式了
默认使用的策略：

public class FileHandler {
public FileLocationStrategy getLocationStrategy() {
return FileLocatorUtils.obtainLocationStrategy(getFileLocator());
}
}

... // 最终调用如下
public final class FileLocatorUtils {

    private static FileLocationStrategy initDefaultLocationStrategy() {
        final FileLocationStrategy[] subStrategies = new FileLocationStrategy[] {
                        new ProvidedURLLocationStrategy(),
                        new FileSystemLocationStrategy(),
                        new AbsoluteNameLocationStrategy(),
                        new BasePathLocationStrategy(),
                        new HomeDirectoryLocationStrategy(true),
                        new HomeDirectoryLocationStrategy(false),
                        new ClasspathLocationStrategy()
                };
        return new CombinedLocationStrategy(Arrays.asList(subStrategies));
    }
}
复制
可以看到它内置的策略，能满足我们几乎所有的case。
其中，我们最常用的ClasspathLocationStrategy放在了末尾，前面的优先级更高。若你想自定义这种扫描顺序，你可以重写对应方法即可~

事件/监听
它也有它自己的一套事件监听的API：
org.apache.commons.configuration2.event.Event：事件。它继承自java.util.EventObject
org.apache.commons.configuration2.event.EventListener：监听器。只有一个方法void onEvent(T event); T extends Event    注意和java.util.EventListener以及
org.apache.commons.configuration2.event.EventSource：事件源。提供注册监听器、移除监听器的两个方法

事件监听的三大要素都有了，这种模式其实我们也已经非常熟悉了。下面主要列出重要的事件、监听器等，然后写个Demo即可
ConfigurationEvent：配置文件的时间。
ConfigurationErrorEvent：配置文件出错的事件
ConfigurationBuilderEvent：builder的事件。
ReloadingEvent：冲洗加载的事件。

常见监听器有：
AutoSaveListener：监听事件为ConfigurationEvent
PropertiesConfigurationLayout：监听事件为ConfigurationEvent
CombinedConfiguration（DynamicCombinedConfiguration）：监听事件为ConfigurationEvent

常见事件源
- EventSource：
- ConfigurationBuilder：
- ReloadingController：
- BaseEventSource：

Demo：使用事件/监听模式记录配置文件的修改记录

关于Commons Configuration的1.x和2.x的简单说明
commons-configuration2是在已经广泛使用的commons-configuration 1.x版本基础上的一个升级版本，与1.x版本并不保持兼容。

从官网的Release History我们可以看到如下：




可以看到1.10版本在2013年10月份就已经寿终正寝了，2.x版本目前还保持着非常活跃的状态。

所以个人建议：若你自己的业务或者框架中需要使用它来管理配置，建议直接使用2.x版本。
但是呢？因为你是直接使用Spring Cloud自带的版本，从而做到jar的最小依赖，你就也有必要了解1.x版本的使用和API了