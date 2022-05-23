![CRANE](https://img.xiajibagao.top/CRANE.png)

基于 SpringBoot 的注解式字典项、关联表与枚举值处理框架。

![CRANE](https://img.shields.io/github/license/Createsequence/crane) ![maven--central](https://img.shields.io/badge/maven--central-0.4.0--alpha1-green)

## 项目介绍

### 1、简介

在我们日常开发中，常常会遇到一些烦人的数据关联和转换问题，比如典型的：

- 对象属性中存有字典 id，需要获取对应字典值并填充到对象中；
- 对象属性中存有外键，需要关联查询对应的数据库表实体，并获取其中的指定属性填充到对象中；
- 对象属性中存有枚举，需要将枚举中的指定属性填充到对象中；

实际场景中这种联查的需求远远不止这些，这些相关代码有时并不方便提取，因此我们不得不重复的写一些的样板代码。

crane 便是为了解决这种烦恼而生。它允许通过统一配置填充的数据源——可以是数据库、缓存，字典或枚举——然后在需要的时候自动根据字段注解去获取并填充到对象中。它本身不产生数据，它只是数据的搬运工，像一个吊车一样将一个数据来源中的数据“转移”到我们指定货对象中，这也正是其名字的由来。

### 2、特性

- 丰富的数据源支持。支持从枚举，普通键值对缓存，或被注解的指定方法中获取数据源，并且允许通过少量代码自定义数据源，以兼容任何数据格式；
- 高度的可扩展型。主要组件都基于接口实现，几乎所有默认组件都允许自行扩展组合并替换；
- 同时支持处理 JsonNode 与普通 JavaBean 对象，并且能够单体或批量的处理各种复杂的嵌套对象；
- 注解式配置。全部配置都支持通过注解完成，并且支持 Spring 元注解，允许自定义各种组合注解；
- 开箱即用。引入依赖即可自动装配所有主要功能的默认配置，并且结合 Spring 提供了诸如自动填充切面等便利的辅助类；

## 文档

更多功能、配置与使用说明，请见仓库的 [Wiki](https://gitee.com/CreateSequence/crane/wikis/pages)。

## 快速开始

### 1、引入依赖

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

> 若无法从 maven 仓库引入 crane 依赖，则可以把代码拉到本地，然后执行 `mvn clean install`命令安装到本地后即可引用。

### 2、引入配置

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

### 3、编码

编写实体类`Person`，并在`sex`字段上添加注解，根据字典项获取字典值，并替换字段值：

~~~java
@Data // 使用Lombok简化getter和setter方法
@Accessors(chain = true)
public class Person {
    @Assemble(namespace = "sex", props = @Prop("sexName"))
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

## 原理

crane 的执行过程就是传统手动数据填充的过程：

>  从待处理对象中获取 key， 然后根据 key 去对应的缓存/接口/数据库中获取对应数据源，将数据源塞入待处理对象

在 crane 中，该过程被抽象为多个步骤，并由多个组件共同完成：

- 操作 `Operation`：对应上述过程描述的一次动作，与类中的一个 key 字段一一对应，表述了本次填充操作对应哪个 key 字段，然后要去何处取对应数据源，接着要如何填充到待处理对象中；
- 类操作配置 `OperationConfiguration`：由一个 `Class`下整合聚合而来，描述了如何处理一类对象，一般与类一对一；
- 操作配置解析器 `OperationConfigurationParser`：用于解析 `Class` 中注解，并生成操作配置；
- 操作执行器 `OperationExecutor`：用于根据操作配置驱动完成对待处理对象的全部填充操作。
- 操作者 `Operator`：用于完成对指定类型数据读取、写入操作的类，是执行上述过程中“获取...塞入...”的主体；
- 操作处理器 `OperateHandler`：类似 Spring 的 Converter ，用于配合操作者完成对不同数据类型的数据的读取与写入；
- 装配源容器 `Container`：也称为数据源容器，一般与一个数据源对应，待处理对象与操作者将在容器中获取数据源，并完成上述填充过程； 

<img src="https://img.xiajibagao.top/image-20220420193512250.png" alt="image-20220420193512250" style="zoom:80%;" />

## 待办

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

- [ ] 允许通过专门的配置类或配置文件配置字段映射模板；

- [ ] 允许通过专门的配置类或配置文件配置操作；
  
- [ ] 添加一个完整的示例项目；

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
  - [ ] 预加载类操作配置到配置缓存；
  - [ ] 枚举容器根据指定包路径批量扫描并注册枚举；

- [ ] 提供粒度更细的，更完善的测试用例；

