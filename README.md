![CRANE](https://img.xiajibagao.top/CRANE.png)

基于 SpringBoot 的注解式字典项、关联表与枚举值处理框架。

![CRANE](https://img.shields.io/github/license/Createsequence/crane) ![maven--central](https://img.shields.io/badge/maven----central-0.5.6-green)

## 项目介绍

**简介**

在我们日常开发中，常常会遇到一些烦人的数据关联和转换问题，比如典型的：

- 对象属性中存有字典 id，需要获取对应字典值并填充到对象中；
- 对象属性中存有外键，需要关联查询对应的数据库表实体，并获取其中的指定属性填充到对象中；
- 对象属性中存有枚举，需要将枚举中的指定属性填充到对象中；

实际场景中这种联查的需求远远不止这些，这些相关代码有时并不方便提取，因此我们不得不重复的写一些的样板代码去完成这些关联数据的查询填充，或字段转换的操作。crane 便是为了解决这种烦恼而生。它允许根据配置，从各种数据源中将数据在处理后自动“转移”到指定对象的自定字段中。

crane 本身不产生数据，它只会像吊车一样“搬运”各种数据，这也正是其名字的由来。

**特性**

- 多样的数据源支持。支持从枚举，普通键值对缓存，类中的指定方法中获取数据源，或通过简单的自定义扩展兼容更多类型的数据源；
- 更强大的字段映射。默认可选 JDK 原生反射或更快的字节码调用，提供不同类型字段之间映射自动转换以及各种集合元素间的字段映射。此外，还支持处理 JsonNode；
- 丰富的扩展功能。提供了类似 spring 的各类 XXXTemplate 的操作模板，基于切面对方法返回值自动填充，SpEL 表达式数据预处理，分组填充，预置字段映射模板，多线程填充等更多扩展功能。
- 支持多种配置方式。提供手动构建与类及属性注解两种配置方式，其中注解还支持用户基于 spring 的元注解机制自由扩展；
- 高度的可扩展性。所有主要组件都基于接口实现，并由 spring 提供了自动装配与依赖管理，用户可以便利的扩展或替换原有组件。

## 文档

更多功能、配置与使用说明，请见仓库的 [**Wiki**](https://gitee.com/CreateSequence/crane/wikis/pages)。

功能的基本使用，可运行的实例，请参考项目的 [**测试用例**](https://gitee.com/CreateSequence/crane/tree/master/crane-spring-boot-starter/src/main/test/java/top/xiajiabagao/crane/starter)。

一些新的点子、意见、建议或者吐槽可以反馈在 [**这里**](https://gitee.com/CreateSequence/crane/issues/I59IUO)。

## 概念

crane 的核心功能就是字段填充。而当我们说“字段填充”，实际上指的就是根据待处理对象中的一个 key 值，获取一个对应的——可能是来自于数据源对象、集合或者其中的一个字段——数据源，然后塞到目标对象的指定字段中。

![image-20220527152623074](https://img.xiajibagao.top/image-20220527152623074.png)

比如上图，就描述了一个根据 `userId` 从数据容器（这里可以简单理解为一个`getById`的方法）中获取到其对应的 `User` 对象，然后将 `User.name 字段值填充到 UserVO.userName`上的填充过程。

在 crane 中，完成上述操作对应如下配置：

~~~java
public class UserVO {
    @Assemble( // 声明一次操作
        container = UserContainer.class, // 根据userId在该容器获得对应的数据源对象User
        props = { @Prop(src = "name", ref = "userName") } // 将user的name映射为UserVO的userName
    )
    private Integer userId; // 外键是userId
    private String userName;
}
~~~

实际业务场景中，数据源可能是各式缓存或者数据库，甚至可能是配置文件，而需要处理的对象有不同的数据结构，要求填充的方式或字段也可能五花八门，而 crane 的核心功能就是对该流程的模拟和增强。

## 快速开始

下面将演示如何最简单的启动一个示例项目。

**引入依赖**

引入 SpringBoot 父工程：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>${version}</scope>
    <relativePath/>
</parent>
```

引入`crane-spring-boot-starter`、`spring-boot-starter`、`spring-boot-starter-web`、`spring-boot-starter-test`，`lombok`依赖：

~~~xml
<dependency>
    <groupId>top.xiajibagao</groupId>
    <artifactId>crane-spring-boot-starter</artifactId>
    <version>${last-version}</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
~~~

> - 若无法从 maven 仓库引入 crane 依赖，则可以把代码拉到本地，然后执行 `mvn clean install`命令安装到本地后即可引用；
> - 由于 Crane 尚未有正式的发行版，因此最好每次引入的依赖都为最新的；

**引入配置**

在启动类添加 `@EnableScane` 注解引入默认配置：

```java
@EnableCrane
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

**编码**

编写实体类`Person`，并在`sex`字段上添加注解，根据字典项获取字典值，并替换字段值：

~~~java
@Data // 使用Lombok简化getter和setter方法
@Accessors(chain = true)
public class Person {
    @Assemble(namespace = "sex", props = @Prop(ref = "sexName"))
    Integer sex;
    String sexName;
}
~~~

创建测试类，并在启动测试用例前向`KeyValueContainer`实例配置字典项`sex`：

~~~java
@SpringBootTest
class CraneApplicationTests {

    @Autowired
    KeyValueContainer keyValueContainer;
    @Autowired
    OperateTemplate operateTemplate;

    @BeforeEach
    void initDate() {
        // 配置字典项sex
        Map<String, Object> gender = new HashMap<>();
        gender.put("0", "女");
        gender.put("1", "男");
        keyValueContainer.register("sex", gender);
    }

    @Test
    void testProcess() {
        Person person = new Person().setSex(0);
        System.out.println("after: " + person); // 处理前
        operateTemplate.process(person);
        System.out.println("before: " + person); // 处理后
    }
    
}
~~~

启动测用例，控制台输出：

~~~java
after: Person(sex=0, sexName=null) // 处理前
before: Person(sex=0, sexName=女) // 处理后
~~~

至此，即完成了 crane 的最基本功能的使用。

## 待办

- [ ] 允许通过专门的配置类或配置文件配置字段映射模板；

- [ ] 允许通过专门的配置类或配置文件配置操作；

- [ ] 更简洁与高效的执行器排序算法；

- [ ] 允许以类似 jackson 的方式对属性中的各个被注解方法的返回值进行拦截处理；

- [ ] 支持 Json 格式的入参填充，比如：

  前端请求入参：

  ~~~json
  {"id": 12}
  ~~~

  后端接口得到参数：

  ~~~json
  {"id": 12, "name": "小明", "sex": 1}
  ~~~

- [ ] 提供支持同时对装配与拆卸操作排序的执行器；

- [ ] 增加一些全局配置项，目前暂定以下几项：

  - [ ] 如果已有字段非空，是否使用填充值覆盖已有字段值；
  - [ ] 当某次填充发生异常时，是否终止本次填充操作；
  - [ ] 是否需要输出填充过程中的一些日志；
  - [x] 预加载类操作配置到配置缓存；
  - [x] 枚举容器根据指定包路径批量扫描并注册枚举；
  - [x] 自定义使用反射还是字节码调用实现对象读写；

- [x] 装卸配置允许处理动态类型的字段；

- [x] 反射调用set方法时，若入参与参数类型不一致，尝试自动转换；

- [x] 提供粒度更细的，更完善的测试用例；

- [x] 提供支持缓存的类注解配置解析器；

- [x] 提供支持多线程处理的执行器；

- [x] 为容器提供一个带有基本方法的抽象类或工具类，简化自定义容器的实现，并且提供如缓存等相关功能扩展；

- [x] 为实现模块提供更多扩展功能，如基于通用 mapper 或 rpc 接口的填充默认容器实现；

- [x] 改造为多模块项目，分离注解模块、核心模块与 Json 和普通 JavaBean 等功能实现模块；

- [x] 操作者中的具体字段处理支持自定义或者通过类似 MessageConverter 的责任链或策略机制进行集中配置；

- [x] 字段配置支持 SpEL 表达式，比如：

  ~~~java
  @Assemble(
      container = DBContainer.class,
      props = @Prop(src = "sex", ref = "name", exp = "#source == '男' ? #target.name + '先生' : #target.name + '小姐'")
  )
  private Integer userId;
  ~~~
  并且基于此提供根据特殊条件判断是否执行本次操作等相关功能；

- [x] 多重嵌套的集合类型字段支持，比如：

  ~~~java
  private List<List<Foo>> nestedProperty;
  ~~~

- [x] 支持集合的元素的批量处理，比如：

  ~~~json
  {"userIds": [1, 2, 3]}
  ~~~

  变成：

  ~~~json
  {
      "userIds": [
          {
              "id": 1,
              "name": "小明"
          },
          {
              "id": 2,
              "name": "小红"
          },
          {
              "id": 3,
              "name": "小刚"
          }
      ]
  }
  ~~~

- [x] 使用字节码类库优化原生反射的性能；

- [x] 引入类似 validation 的 group 的概念，用于将需要处理的字段分组，从而允许指定并仅使用特定分组的操作配置；

- [ ] ~~允许通过多个 key 字段获取数据源；~~

