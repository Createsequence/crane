# crane

![CRANE](https://img.xiajibagao.top/CRANE.png)

基于 SpringBoot 的注解式字典项、关联表与枚举值通用处理框架。

## 一、项目介绍

### 1、简介

在我们日常开发中，常常会遇到一些烦人的关联数据处理和转换问题，比如各种 id 传到前端时需要联查相关数据，01这样的字段需要转成男女......等等，这些数据并不具参与后台实际的业务逻辑，但是我们却不得不为此多写一些重复的查询与字段填值代码。

为了避免这种情况，我希望能有个统一的地方配置填充的数据源——不管是数据库、字典还是枚举还是其他什么——然后通过字段注解去自动获取并填充到对象中，为了实现这个功能，在公司的时候我分别开发了一套字典项自动填充框架，与关联表字段信息自动填充框架，这两套框架在使用中暴露了一些问题，经过总结与重新设计，于是有了 crane。

crane 本身不产生数据，它只是数据的搬运工，像一个吊车一样将一个数据来源中的数据“转移”到我们指定货对象中，这也正是其名字的由来。

### 2、特性

crane 适用于处理以下情况：

**处理关联表**

有一个 id 字段，但是返回前端的时候需要关联查询带出对应的信息：

~~~json
{
    "userId": 1
}
~~~

处理后：

~~~json
{
    "user": {
        "name": "小明",
        "age": 12
    }
}
~~~

**处理枚举值**

有一个枚举字段，但是返回前端时需要返回枚举值json对象或者只返回枚举值：

~~~json
{
    "season": "SPRING"
}
~~~

处理后：

~~~json
{
    "season": {
        "code": 1,
        "value": "春天"
    }
}
~~~

**处理字典项**

有一个字典项字段，但是返回前端时需要转成对应的字典值：

~~~json
{
    "sex": 1
}
~~~

处理后：

~~~json
{
    "sex": "男"
}
~~~



**支持嵌套结构的 Json 与 JavaBean**

crane 支持批量处理 Json 对象中的嵌套的 Json 数组与 Json 对象类型属性，也支持批量处理普通 JavaBean 中的嵌套的集合类型与对象类型属性。

### 3、原理

**核心概念**

- **容器** `Container`：表示某类特定的数据来源，比如内置的`top.xiajibagao.crane.container.KeyValueContainer`就是一个简单的键值对容器，通过操作者，我们可以获取对象指定指定的某个特定的属性值作为 key，并从中获取数据源；

- **操作者** `Operator`：表示用于处理对象实例的类，又根据功能的不同又有所区分：，

  1. 装配器`Assembler`：用来从对象实例中获取 key 值，并在从容器中获取数据源后将其填充到对象实例中。

     比如内置的 `top.xiajibagao.crane.impl.json.JacksonAssembler`就是专门用于处理 Json 对象的装配器；

  2. 拆卸器`Disassembler`：用于将对象实例中嵌套的对象取出的类，

     比如内置的 `top.xiajibagao.crane.impl.json.JacksonDisassembler`就是专门用于从 Json 对象中获取其嵌套的 Json 对象字段或 Json 数组字段的拆卸器；

- **操作配置** `Operation`：根据左右域不同分为两类：

  1. 属性操作配置`PropertyOperation`：一般与属性上的注解一一对应，包含了指定的容器与操作者，以及相关的操作配置，表明操作者将从哪个容器中如何获取数据源，并且在获取数据后如何填充到当前指定对象中；
  2. 类操作配置`OperationConfiguration`：与待处理的类一一对应，内部包含了全部的属性操作配置；

- **配置解析器** `OperationConfigurationParser`：用于解析类中注解，并且将其转换为响应操作配置的类；

- **动作执行器**`OperationExecutor`： 用于根据操作配置完成整个数据处理流程；

**运行流程**

![运行流程与总体结构](https://img.xiajibagao.top/image-20220304091120606.png)

- 我们在类与类属性上通过注解配置要如何处理一个字段：从哪个容器`Container`中获取数据源，要使用什么操作者`Operator`，以什么样的方式去将数据源中的数据填充到我们的对象实例中；
- 解析器`OperationConfigurationParser`解析类中的注解，分别获取类中每个字段配置的操作者与数据源以及其他配置，将其整合为一个属性操作配置对象 `PropertyOperation`，同一类下的属性操作配置对象最终整合为一个类操作配置对象 `OperationConfiguration`；
- 当需要执行数据填充操作时，将待处理对象实例与解析获得的对应类操作配置对象放入操作执行器 `OperationExecutor`，执行器将从配置中获取全属性的操作配置，然后依次执行，最终将数据处理并填充到对象实例中；

## 二、快速开始

首先，创建一个空的 SpringBoot 工程。

### 1、添加依赖

引入 SpringBoot 父工程：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>${version}</scope>
    <relativePath/>
</parent>
```

引入`scane`、`spring-boot-starter`、`spring-boot-starter-web`、`spring-boot-starter-test`依赖：

~~~xml
<dependency>
    <groupId>top.xiajibagao</groupId>
    <artifactId>scane</artifactId>
    <version>${version}</version>
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
~~~

> 若无法从 maven 仓库引入 scane 依赖，则可以把代码拉到本地，然后执行 `mvn clean install`命令安装到本地后即可引用。

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
@Data // 这里使用Lombok简化getter和setter方法
@Accessors(chain = true)
public class Person {
    @Assemble(container = KeyValueContainer.class, namespace = "sex")
    Integer sex;
}
~~~

创建测试类，并在启动测试用例前向`KeyValueContainer`实例配置字典项`sex`：

~~~java
@SpringBootTest
class CraneApplicationTests {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    OperationExecutor operationExecutor;
    
    @BeforeEach
    void initDate() {
        // 配置字典项sex
        Map<String, Object> gender = new HashMap<>();
        gender.put("0", "女");
        gender.put("1", "男");
        keyValueContainer.register("sex", gender);
    }
    
    @Test
    void testSimpleJsonKV() {
        Person jsonPerson = new Person().setSex(0);
        JsonNode jsonNode = objectMapper.valueToTree(jsonPerson);

        OperationConfiguration jsonConfig = configurationParser.parse(Person.class, jacksonOperatorFactory);
        System.out.println(jsonNode); // 处理前
        operationExecutor.execute(Collections.singletonList(jsonNode), jsonConfig);
        System.out.println(jsonNode); // 处理后
    }
    
}
~~~

启动测用例，控制台输出：

~~~
{"sex":0} // 处理前
{"sex":"女"} // 处理后
~~~

### 4、小结

通过以上步骤，我们了解了 `crane`的基本使用，仅需引入依赖，启用配置，即可使用配置好的字典项自由填充。实际上它的功能远远不止如此。

## 三、使用

### 1、容器

容器是 crane 的核心组件之一，用于为操作者提供数据源，换而言之，就是提供我们填充字段时使用的数据的。我们需要有数据源才能进行后续操作。由于希望能够最大限度的兼容各种数据来源，因此 crane 的容器机制基于接口实现，并且仅提供两个比较常用的默认容器。

#### **键值对容器**

项目默认提供键值对容器`top.xiajibagao.crane.container.KeyValueContainer`，项目启动时会默认将实例注入 spring 容器，当我们需要使用前，需要获取该实例并向其中配置好我们需要的数据。

比如现在我们希望配置性别这个字段，则有需要：

~~~java
@PostConstruct
public void initDate() {
    // 配置字段配置
    Map<String, Object> gender = new HashMap<>();
    gender.put("0", "女");
    gender.put("1", "男");
    // 从spring中获取容器，并且将配置放入命名空间sex
    keyValueContainer.register("sex", gender);
}
~~~

然后在类中的指定字段上引用即可：

~~~java
// 指定容器KeyValueContainer与命名空间sex
@Assemble(container = KeyValueContainer.class, namespace = "sex")
private Integer sex;
~~~

当执行时，会先获取 `sex` 字段值，然后去指定的 `KeyValueContainer` 容器的命名空间`sex`找到对应的值，并进行处理。

#### **枚举容器**

项目默认提供枚举容器`top.xiajibagao.crane.container.EnumDictContainer`，项目启动时会默认将实例注入 spring 容器，同样的，当我们需要使用前，需要获取该实例并向其中配置好我们需要的枚举。

比如，现在我们有一个性别枚举：

~~~java
@Getter
@RequiredArgsConstructor
public enum Gender {
    MALE(1, "男"),
    FEMALE(2, "女");
    private final Integer id ;
    private final String name;
}
~~~

然后去 spring 里面的`EnumDictContainer`注册这个枚举：

~~~java
@PostConstruct
public void initDate() {
    // 指定枚举命名空间为“sex”，并且通过枚举名来寻找枚举实例
    enumDictContainer.register(Gender.class, "sex", Enum::name); 
}
~~~

接着，在类中如此引用即可：

~~~java
@Assemble(container = EnumDictContainer.class, namespace = "sex")
private Gender gender;
~~~

当然，如果我们希望只引用枚举中的某些特定字段值，这也是支持的，具体参见后文的字段配置部分。

#### **通用方法容器**

由于实际场景中可能存在大量需要通过 Mapper/service 或 RPC 接口根据 id 查询的情景，因此，若针对每一个接口都需要创建一个容器提供数据源是一件繁琐的事情，为此，crane 提供了通用方法容器`top.xiajibagao.crane.extend.container.MethodContainer`，用于通过注解快速将查询接口接入容器。

**基于方法注解**

crane 提供了 `top.xiajibagao.crane.annotation.extend.ContainerMethodBean`使用该注解标记需要的 Mapper/service 或其他任何被 Spring 管理的实例所对应的类，再使用`top.xiajibagao.crane.annotation.extend.ContainerMethod` 标记其中的方法即可：

~~~java
@ContainerMethodBean
public class UserService {
    
    // 通过“user”寻找对应的实例，然后指定返回值类型为 User.class, key 字段为 id
    @ContainerMethod(namespace = "user", sourceType = User.class, sourceKey = "id")
    public List<User> getByIds(List<Integer>) {
        // 返回user对象集合
    }
}
~~~

当使用时，在类对象中引用即可：

~~~java
@Assemble(container = MethodContainer.class, namespace = "user")
private Integer userId;
~~~

当实际调用时，将会在`UserManager`容器中找到`UserManager.getByIds`方法，然后传入 `userId`查询集合，再按指定的 “id” 字段对结果分组，最后作为数据源交由操作者处理。

**基于类注解**

当目标方法位于父类时，无法通过`ContainerMethod`直接注明指定方法，如果为此专门在所有的实现类里重写方法又显得繁琐，因此，也可以直接在`ContainerMethodBean`注解中声明方法。

我们依然以上述获取用户的方法为例，但是`UserService`的 `getByIds`方法通过继承 `BaseService` 得到的，我们可以这么写：

~~~java
@ContainerMethodBean({
    @ContainerMethodBean.Method(
        namespace = "user",
        name = "getByIds", returnType = User.class, paramTypes = List.class,
        sourceType = User.class, sourceKey = "id"
    )
})
public class UserService extend BaseService<User> {
}
~~~

该写法依然等效于上述例子。

#### **自定义容器**

当我们的数据源来自于数据库查询，或者默认提供的容器不满足需求时，可以自定义容器。

自定义容器需要实现 `top.xiajibagao.crane.container.Container`接口，并且注册到 spring 中，接着在`@Assemble#container()`方法引用即可。

这里给一个示例，假设我们现在有一个字段`userId`，我们需要从`User`表查出需要的数据，因此我们可以自定义一个容器：

~~~java
@Component
public class UserContainer implements Container {

    @Autowrite
    prvate UserService userService;
    
    @Override
    public void process(List<Object> targets, List<AssembleOperation> operations) {
        for (Object target : targets) {
            operations.forEach(operation -> {
                // 通过操作者获取注解字段的值，在这里也对应userId
                Object key = operation.getAssembler().getKey(target, operation);
                if (Objects.isNull(key)) {
                    return;
                }
                // 模拟从服务中根据id获取User对象
                User user = userService.getById(Integer.valueOf(key.toString()));
                if (Objects.nonNull(beam)) {
                    // 通过操作者将User对象的数据填充到对象实例
                    operation.getAssembler().execute(target, beam, operation);
                }
            });
        }
    }

}
~~~

最后我们把这个容器注册到 spring，然后在类中引用即可：

~~~java
@Assemble(container = UserContainer.class)
private Integer userId;
~~~

这个容器将获取每一个实例对象需要关联的用户id，然后同`userService`根据 id 查询对应的`User`，然后根据配置将其数据填充到对象实例中。

由于通过`AssembleOperation`可以轻松获取到待处理数据实际类型，以及一些全局配置信息，因此如果项目基于 JPA 或者 mybatis-plus 这类框架开发，也可以借助通用 Mapper 层实现一个通用的查询容器。

**通用容器模板**

由于大部分的容器其实操作基本不外乎四步：

1. 从待处理的对象中获取 key / namespace ；
2. 根据 key / namespace 集合查询获取数据源；
3. 查询出的数据源按 key  / namespace 分组 ；
4. 将分组的数据源处理后填充到对应的待处理对象中；

因此 crane 也提供了抽象模板用于简化操作：

- `top.xiajibagao.crane.extend.container.BaseKeyContainer`：基于 key 的容器；
- `top.xiajibagao.crane.extend.container.BaseNamingContainer`：基于 key 和 namespace 的容器；

### 2、处理字段

假如我们有一个已经注册 spring 的 `UserContainer`容器，他提供`User`实例作为数据源：

~~~java
@Getter
@RequiredArgsConstructor
public enum User {
    private Integer id;
    private String name;
}
~~~

#### **普通字段**

若我们现有字段 `user`对应希望关联的 User 对象的 id，我们可以简单的直接配置使用：

~~~java
@Assemble(container = UserContainer.class)
private Integer user = 1;

// 处理后得到
{
    "user": {
        "id": 1,
        "name": "小明"
    }
}
~~~

当然，也可以指定将**数据源的对象直接赋值给指定字段**（若是 Json 则可以添加原本不存在的字段）：

~~~java
@Assemble(container = UserContainer.class, props = @Prop("userInfo"))
private Integer user = 1;

// 处理后得到
{
    "user": 1,
    "userInfo": {
        "id": 1,
        "name": "小明"
    }
}
~~~

由于数据源是对象，因此我们也可以将**对象中的指定属性映射到目标实例的指定属性**，比如：

~~~java
@Assemble(container = UserContainer.class, props = {
    @Prop(src = "name", ref = "userName"),
    @Prop(src = "id", ref = "userId")
})
private Integer user = 1;

// 处理后得到
{
    "user": 1,
    "userId": 1,
    "userName": "小明"
}
~~~

如果我们愿意的话，也可以同时保留上述三种形式：

~~~java
@Assemble(container = UserContainer.class, props = {
    @Prop("user"),
    @Prop(src = "name", ref = "userName"),
    @Prop(src = "id", ref = "userId")
})
private Integer user = 1;

// 处理后得到
{
    "user": {
        "id": 1,
        "name": "小明"
    },
    "userId": 1,
    "userName": "小明"
}
~~~

#### **嵌套字段**

crane 支持对类中嵌套的对象及集合类型进行处理，只需对该类型字段添加注解 `top.xiajibagao.crane.annotation.Disassemble`即可。

如果该字段是对象：

~~~java
@Disassemble(Foo.class)
private Foo foo; 
~~~

如果是集合：

~~~java
@Disassemble(Foo.class)
private List<Foo> foos; 
~~~

>  **注意：目前对集合仅支持单层嵌套，即不支持集合中套集合多层嵌套的写法**

举个例子：

现在我们有一个 Person，并且已经向 spring 容器中的 `KeyValueContainer` 配置了 `sex`相关配置：

~~~java
@Accessors(chain = true)
@Data
public class Person {
    
    String name;

    @Assemble(container = KeyValueContainer.class, namespace = "sex")
    Integer sex;
    
    @Disassemble(Person.class)
    List<Person> relatives;

}
~~~

现在执行代码：

~~~java
Person jsonPerson = new Person().setName("小明");
jsonPerson.setRelatives(Arrays.asList(
    new Person().setName("小明爸").setSex(1),
    new Person().setName("小明妈").setSex(0)
));

JsonNode jsonNode = objectMapper.valueToTree(jsonPerson);

OperationConfiguration jsonConfig = configurationParser.parse(Person.class, jacksonOperatorFactory);
System.out.println(jsonNode); // 处理前
operationExecutor.execute(Collections.singletonList(jsonNode), jsonConfig);
System.out.println(jsonNode); // 处理后

// 处理前
{
    "name":"小明","sex":1,
    "relatives":[
        {"name":"小明爸","sex":1},
        {"name":"小明妈","sex":0}
    ]
}
// 处理后
{
    "name":"小明","sex":"男",
    "relatives":[
        {"name":"小明爸","sex":"男"},
        {"name":"小明妈","sex":"女"}
    ]
}
~~~

>  **注意：无法处理对象实例循环引用的情况（即 A 实例引用 B 实例，B 实例又引用了 A 实例）！！！**
>
>  **注意：无法处理对象实例循环引用的情况（即 A 实例引用 B 实例，B 实例又引用了 A 实例）！！！**
>
>  **注意：无法处理对象实例循环引用的情况（即 A 实例引用 B 实例，B 实例又引用了 A 实例）！！！**

#### **配置模板**

`Assemble#props()`可以很方便的用来配置数据源与对象实例间的字段映射，但是当需要配置的字段映射很多，且需要处理的字段也很多时，就会导致注解膨胀，让我们的代码变得臃肿。

因此，可以通过`top.xiajibagao.crane.annotation.PropsTemplate`注解，将字段映射配置在另外的类上，然后通过`Assemble#propTemplate()`对类进行引用。

比如，我们原本有这样一个配置：

~~~java
@Assemble(
    container = UserContainer.class,
    props = {
        @Prop(src = "age", ref = "userAge"),
        @Prop(src = "name", ref = "userName"),
        @Prop(src = "sex", ref = "userSex")
    }
)
private Integer UserId;
~~~

我们可以创建一个字段配置模板，用于存储一部分配置：

~~~java
@PropsTemplate({
    @Prop(src = "name", ref = "userName"),
    @Prop(src = "sex", ref = "userSex")
})
public interface UserPropTemplate{}
~~~

现在使用下述配置就能实现跟之前一样的效果：

~~~java
@Assemble(
    container = UserContainer.class, 
    props = @Prop(src = "age", ref = "userAge"),
    propTemplates = UserPropTemplate.class
)
private Integer UserId;
~~~

#### **字段排序**

默认情况下，执行器会按字段在类中声明的先后顺序对字段进行处理，但是也可以通过`top.xiajibagao.crane.annotation.Sort`注解对字段的处理顺序进行排序，比如：

~~~java
@Sort(3)
@Assemble(container = ExampleContainer.class)
private Integer deptManagerId;
@Sort(2)
@Assemble(container = ExampleContainer.class)
private String deptId;
@Sort(1)
@Assemble(container = ExampleContainer.class)
private Integer userId;
~~~

使用`Sort`注解后，三个字段将按注解值从小到大排序，依次处理 `userId` => `deptId` =>`deptManagerId`。

该功能主要用于解决无法通过一个容器获取全部的数据源，先查出一个前置 id，然后才能通过另一个容器根据前置 id 查询出后续数据的情景。

一个比较典型的情景是需要先通过用户 id 查出用户归属部门 id，然后再根据用户归属部门 id 查询出部门领导信息。

> 注意：排序功能需要执行器提供支持，默认提供了按排序执行的执行器`top.xiajibagao.crane.operator.SequentialOperationExecutor`

### 3、操作者

操作者 `Operator` 是实现 crane 对不同数据类型处理的核心，与容器一样，出于对不同类型数据兼容的考虑，crane 提供三个顶层接口用于实现：

- `top.xiajibagao.crane.operator.interfaces.Assembler`：与 `@Assemble`注解对应，用于获取 key 数据与处理容器中获取的数据源；
- `top.xiajibagao.crane.operator.interfaces.Disassembler`：与 `@Disassemble`注解对应，用于将提取实例中的嵌套字段数据；
- `top.xiajibagao.crane.operator.interfaces.OperatorFactory`：用于生产上述两接口的实现类实例；

此外，提供了`top.xiajibagao.crane.impl.json`包的基本实现用于处理 JsonNode 对象，以及`top.xiajibagao.crane.impl.bean`包的基本实现用于处理普通 JavaBean。

当使用时，仅需在提供一个 `OperatorFactory`实例，在调用`OperationConfiguration#parse()`时放入即可：

~~~java
// 用于生成 JacksonAssembler 与 JacksonDisassembler
OperatorFactory jacksonOperatorFactory = new JacksonOperatorFactory();
// 用于生成 BeanReflexAssembler 与 BeanReflexDisassembler
OperatorFactory beanReflexOperatorFactory = new BeanReflexOperatorFactory();
// 解析时根据需要选择不同的 OperatorFactory 即可
OperationConfiguration jsonConfig = configurationParser.parse(Person.class, jacksonOperatorFactory);
~~~

同理，若有当前的操作者不符合需求，比如希望能处理 `FastJson`产生的 Json 对象，则仅需要提供 `FastJson` 版的 `Assembler` 与 `Disassembler`，然后再提供一个能够生产这两者的`OperatorFactory`即可。

### 4、配置解析器

解析器对应的顶层接口为`top.xiajibagao.crane.parse.interfaces.OperateConfigurationParser`，其主要用于解析类注解并生成`OperationConfiguration`。

提供了默认的实现类`top.xiajibagao.crane.parse.BeanOperateConfigurationParser`，该实现类提供了对所有默认注解的解析支持，并且主要的关键方法都使用`protected`修饰以便于子类重写。

因此，若有扩展的需求，推荐基于该实现重写，否则需要注意是否会影响到原有注解的解析。

### 5、配置执行器

执行器对应的顶层接口为`top.xiajibagao.crane.operator.interfaces.OperationExecutor`，其主要用于根据解析出的配置`OperationConfiguration`对数据进行处理。

默认提供三个实现类：

- `top.xiajibagao.crane.operator.SequentialOperationExecutor`：有序且同步的执行器，会按照`top.xiajibagao.crane.annotation.Sort`注解指定的顺序处理字段，由于为了保证顺序，对同一批数据进行处理时，可能会多次访问同一个容器；
- `top.xiajibagao.crane.operator.UnorderedOperationExecutor`：无序且同步的执行器，不会按照`top.xiajibagao.crane.annotation.Sort`注解指定的顺序处理字段，由于不需要保证顺序，对同一批数据进行处理时，同一个容器仅需访问一次；
- `top.xiajibagao.crane.operator.AsyncUnorderedOperationExecutor`：`UnorderedOperationExecutor`的异步版，不同之处在于不同容器之间的访问是并行进行的。该容器默认不注册到 Spring，需要自行启用。

若有其他需求同样可以自行实现接口。

## 四、扩展功能

以下功能为非通用功能，需要自行启用。

### 1、自定义注解

crane 通过`org.springframework.core.annotation.AnnotatedElementUtils`实现了元注解的功能，任何带有 `top.xiajibagao.crane.annotation.MateAnnotation`注解标记的注解都可以将其作为元注解。

比如，假设现在我们有一个关于用户关联信息填充的复制配置：

~~~java
@Assemble(
    container = DBContainer.class, 
    namespace = "user",
    props = {
        @Prop(src = "age", ref = "userAge"),
        @Prop(src = "name", ref = "userName"),
    },
    propTemplates = {
        UserRoleAssembleTemplate.class, 
        UserDeptAssembleTemplate.class
    }
)
private Integer UserId;
~~~

由于很多类都需要引入这些配置，这会导致代码变得十分臃肿，因此我们可以创建一个注解，并且将原本的注解作为元注解放到新注解上：

~~~java
@Assemble(
    container = DBContainer.class, 
    namespace = "user",
    props = {
        @Prop(src = "age", ref = "userAge"),
        @Prop(src = "name", ref = "userName"),
    },
    propTemplates = {
        UserRoleAssembleTemplate.class, 
        UserDeptAssembleTemplate.class
    }
)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AssembleUserInfo {
}
~~~

现在我们只需要在类中以如此方式使用即可得到跟原本一样的效果：

~~~java
@AssembleUserInfo
private Integer UserId;
~~~

### 2、方法返回值处理切面

针对普通的 JavaBean 填充，crane 提供了一个注解`top.xiajibagao.crane.impl.bean.aop.ProcessResult`，与拦截注解的切面`top.xiajibagao.crane.impl.bean.aop.MethodResultProcessAspect`。

将切面注册到 spring 后，我们可以在方法上添加如下注解，切面将自动根据注解配置拦截返回值并进行处理：

~~~java
@ProcessResult(
    targetClass = Foo.class,
    parser = BeanOperateConfigurationParser.class,
    operatorFactory = BeanReflexOperatorFactory.class,
    executor = UnorderedOperationExecutor.class
)
public List<Foo> listFooById(Integer id) {
    // 具体实现
}
~~~

注解`ProcessResult`同样可以作为元注解使用。

此外，该注解支持通过 `ProcessResult#condition()`属性根据一个返回布尔值的 SpEL 表达式执行结果选择是否执行填充，比如：

~~~java
@ProcessResult(targetClass = Foo.class, condition = "#id != null")
public List<Foo> listFooById(Integer id) {
    // 具体实现
}
~~~

按上述写法，当 id 为 null 时将不执行填充。

### 3、全局序列化配置

针对 `SpringBoot`的 `@RequestBody`或 `@RestController`注解，提供`top.xiajibagao.crane.impl.json.module.ProcessJson`注解和`top.xiajibagao.crane.impl.json.module.CraneDynamicJsonModule`模块用于配置全局的 Json 序列化配置。

首先将 module 注册到全局序列化使用的 `ObjectMapper`中：

~~~java
@Bean
public ObjectMapper objectMapper(BeanFactory beanFactory) {
    ObjectMapper globalSerialMapper = new ObjectMapper();
    ObjectMapper processMapper = new ObjectMapper(); // globalSerialMapper 与 processMapper 不能是同一个实例，请务必注意！
    globalSerialMapper.registerModule(new CraneDynamicJsonModule(processMapper, beanFactory));
    return globalSerialMapper;
}
~~~

> **注意：CraneDynamicJsonModule 创建时也需要一个 ObjectMapper 实例，该实例不可以与 Module 要注册的 ObjectMapper 实例相同，否则将序列化时将进入死循环！**

然后，在需要进行处理的类上注解：

~~~java
@ProcessJson(
    targetClass = Foo.class,
    parser = BeanOperateConfigurationParser.class,
    operatorFactory = BeanReflexOperatorFactory.class,
    executor = UnorderedOperationExecutor.class
)
@Accessors(chain = true)
@Data
public class BeanPerson {
    
}
~~~

如此，当返回给前段的数据在序列化时，就会先解析注解配置并对 JsonNode 对象进行处理。

## 五、待开发功能

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

- [ ] 允许以类似 jackson 的方式对属性中的各个被注解方法的返回值进行拦截处理；

- [ ] 支持集合的元素的批量处理，比如：

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

- [ ] 多重嵌套的集合类型字段支持，比如：

  ~~~java
  private List<List<Foo>> nestedProperty;
  ~~~

- [ ] 更简洁与高效的执行器排序算法；

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

- [ ] 增加一些全局配置项，比如字段解析异常时是否继续对当前对象解析等；

- [ ] 完善测试用例；
  
- [ ] 发布v1.0.0版本，并提供完整的文档；