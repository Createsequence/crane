![CRANE](https://img.xiajibagao.top/CRANE.png)

基于 SpringBoot 的注解式字典项、关联表与枚举值处理框架。

![CRANE](https://img.shields.io/github/license/Createsequence/crane) ![maven--central](https://img.shields.io/badge/maven--central-0.2.0--alpha1-green)

## 一、项目介绍

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

### 3、原理

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

## 二、快速开始

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
    @Assemble(container = KeyValueContainer.class, namespace = "sex", props = @Prop("sexName"))
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
    OperateHelper operateHelper;

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
        operateHelper.process(person);
        System.out.println("before: " + person); // 处理后
    }
    
}
~~~

启动测用例，控制台输出：

~~~java
after: Person(sex=0, sexName=null) // 处理前
before: Person(sex=0, sexName=女) // 处理后
~~~

至此，即完成了 crane 的基本功能的使用，更多功能请见下文。

## 三、数据源容器

在 crane 中，数据源来自指定的数据源容器，而数据源容器即指 `top.xiajibagao.crane.core.container.Container`接口的各项实现类，它被用于提供各种来源的数据源。

当使用时，需要预先将容器注册到 Spring 中，然后即可通过 `@Assemble`的`Assemble#container()`属性根据容器类型、或通过`Assemble#containerName()`根据容器在 Spring 中的 Bean 名称获取容器。

默认情况下，crane 提供以下四种实现：

- 枚举容器：`top.xiajibagao.crane.core.container.EnumDictContainer`
- 键值对容器：`top.xiajibagao.crane.core.container.KeyValueContainer`
- 方法容器：`top.xiajibagao.crane.extend.container.MethodContainer`
- 内省容器：`top.xiajibagao.crane.extend.container.IntrospectContainer`

下文将介绍其作用与对应的使用方式。

### 1、枚举容器

枚举容器`EnumDictContainer`用于处理枚举类型的数据源。

使用前，需要预先通过`EnumDictContainer#register()`方法项容器注册枚举类，指定货枚举类型与其对应在容器中的 namespace，以及枚举项对应的 key 值。

比如，目前我们有一个枚举类 `Gender`：

~~~java
public enum Gender {
    MALE(1, "男"),
    FEMALE(2, "女");
    private final Integer id ;
    private final String name;
}
~~~

我们可以根据需要选择不同的方法重载将其注册到容器中：

~~~java
// namespace为gender，并且以枚举项的id属性作为key值
enumDictContainer.register(Gender.class, "gender", Gender::id);
// namespace为Gender类的非全限定名Gender，并且以枚举项的 Enum#name() 返回值作为key值
enumDictContainer.register(Gender.class);
~~~

当使用时，在 `@Assemble`注解中引用即可：

~~~java
@Assemble(
    container = EnumDictContainer.class, // 指定使用枚举容器
    namespace = "gender", // namespace为上文指定的gender
    props = @Prop(src = "name", ref = "genderName") // 获取Gender枚举中的name字段值，并填充到genderName字段
)
private Integer gender;
private String genderName;
~~~

需要注意的是，枚举容器底层实际上是基于枚举字典 `top.xiajibagao.crane.core.helper.EnumDict`实现，该类默认是单例的，当需要同时启用多个枚举容器时，需要注意不要使用了同一个枚举字典实例。



### 2、键值对容器

键值对容器`KeyValueContainer`基于一个双重 Map 集合实现，本质上是一个基于本地缓存的数据源。

与枚举容器类似，键值对容器使用前也需要预先注册所需的枚举值，然后在字段注解上通过 namespace 与 key 进行引用。

比如，我们需要处理很典型的性别字典项，则如此注册：

~~~java
Map<String, Object> gender = new HashMap<>();
gender.put("0", "女");
gender.put("1", "男");
keyValueContainer.register("sex", gender);
~~~

然后再在待处理对象中引用：

~~~java
@Assemble(
    container = keyValueContainer.class, // 指定使用键值对容器
    namespace = "sex", // namespace为上文指定的sex
    props = @Prop("sexName") // 从命名空间sex中根据sex字段值获取对应的value，并填充到sexName字段
)
private Integer sex;
private String sexName;
~~~



### 3、方法容器

方法容器`MethodContainer`是基于 namespace 隔离，将各个类实例中的方法作为数据源的容器。

**注册方法**

在使用方法容器之前，我们需要先使用 `top.xiajibagao.annotation.@MethodSourceBean.Method`注解作为数据源的方法，然后再使用`top.xiajibagao.annotation.@MethodSourceBean`注解该方法所在的类实例。

比如，我们需要将一个根据用户 id 批量查询用户对象的接口方法作为数据源：

~~~java
@MethodSourceBean
public class UserService {
    // 通过“user”寻找对应的实例，然后指定返回值类型为 User.class, key 字段为 id
    @MethodSourceBean.Mehtod(namespace = "user", sourceType = User.class, sourceKey = "id")
    public List<User> getByIds(List<Integer> ids) {
        // 返回user对象集合
    }
}
~~~

当然，如果这个方法来自与父类，无法显示的使用注解声明数据源方法，也允许通过类注解声明：

~~~java
@ContainerMethodBean({
    @ContainerMethodBean.Method(
        namespace = "user", name = "getByIds", sourceType = User.class, sourceKey = "id"
    )
})
public class UserService extend BaseService<User> {}
~~~

当项目启动时，crane 将从 Spring 容器中获取被 `@ContainerMethodBean`注解的类，并获取其中被注解的方法，并根据指定的 namespace 注册到方法容器对应的命名空间。

**使用**

当我们使用时，与其他容器保持一致：

~~~java
@Assemble(
    container = MethodContainer.class, // 指定使用键值对容器
    namespace = "user", // namespace为上文指定的user
    props = @Prop("userBean") // 从命名空间user中获取方法getByIds，然后将userId对应的user对象填充到userBean字段中
)
private Integer userId;
private User userBean;
~~~

当有一批的待处理对象时，crane 将批量的把 userId 字段值从对象中取出，并组成集合后再传入 `getByIds`方法，以此类推，由于作为 key 字段的类型也没有任何限制，因此作为 key 的字段是集合或是对象也是允许的。

crane 传给数据源方法参数的总是 key 类型的集合，然后从方法获取的一批数据源总是以 `@ContainerMethodBean.Method#sourceKey` 分组，因此作为**数据源的方法入参必须有且仅有一个 `Collection`或Collection子类的参数，返回值也必须为 Collection 或 Collection**。

**一对多**

默认情况，crane 总是认为数据源方法返回的集合中的对象与 key 字段的值是**一对一**的，当数据源方法返回的集合与 key 字段对应的值是**一对多**时，需要在方法注解上声明。

比如我们现在有一批待处理的 `Classroom` 对象，需要根据 `Classroom#id`字段批量获取`Student`对象，然后根据`Student#classroomId`字段填充到对应的 `Classroom` 对象中：

~~~java
@MethodSourceBean.Mehtod(
    namespace = "student", 
    sourceType = Student.class, sourceKey = "classroomId",
    mappingType = MappingType.ONE_TO_MORE // 声明待处理对象跟Student通过classroomId构成一对多关系
)
public List<Student> listStudentByClassroomIds(List<Integer> classroomIds) {
    // 查询Student对象
}
~~~

然后在待处理对象中引用：

~~~java
@Assemble(
    container = MethodContainer.class,
    namespace = "student",
    props = @Prop("students")
)
private Integer classroomId;
private List<Student> students;
~~~



### 4、内省容器

内省容器`IntrospectContainer`的数据源就是待处理对象本身，它用于需要对待处理对象本身进行处理的情况。

比如简单的同步一下字段：

~~~java
// 将对象中的name字段的值同步到userName字段上
@Assemble(container = IntrospectContainer.class, props = @Prop("userName")
private String name;
private String userName;
~~~

也可以用于处理集合取值：

~~~java
// 将对象中的users集合中全部name字段的值同步到userNames字段上
@Assemble(container = IntrospectContainer.class, props = @Prop(src = "name", ref = "userNames"))
private List<User> users;
private List<String> userNames;
~~~

或者配合 SpEL 预处理数据源的功能处理一些字段：

~~~java
@Assemble(
    container = IntrospectContainer.class, props = @Prop(
        ref = "userName", 
        exp = "sex == 1 ? #source.name + '先生' ： #source.name + '女士'", // 根据性别，在name后追加“先生”或者“女士”
        expType = String.class
    )
)
private String sex;
private String name;
~~~



### 5、自定义容器

一般情况下，以上四种容器可以满足大部分情况下的需求，但是如果有需要，也可以实现`top.xiajibagao.crane.core.container.Container`接口自定义一个容器。

**基本使用**

比如，我们现在需要一个根据待处理对象中的 key，查询出 User 对象并回填对应字段的容器：

~~~java
@Component
public class UserContainer implements Container {

    @Autowrite
    private UserService userService;

    @Override
    public void process(MultiValueMap<AssembleOperation, ?> operations) {
        // 从待处理对象中获取 key
        Set<Integer> userIds = new HashSet<>();
        operations.forEach((operation, targets) -> targets.forEach(target -> {
            Object key = operation.getAssembler().getKey(target, op);
            Integer actualKey = parseKey(key);
            userIds.add(actualKey);
        }));

        // 根据 key 获取数据源，并按 id 分组
        Map<Integer, User> sources = userSevice.listByIds(userIds).stream()
            .collect(Collectors.toMap(User::getId, Function.identity()));

        // 将数据源回填充至待处理对象
        operations.forEach((operation, targets) -> targets.forEach(target -> {
            Object key = op.getAssembler().getKey(t, op);
            Integer actualKey = parseKey(key);
            User user = sources.get(actualKey);
            operation.getAssembler().execute(target, user, operation);
        }))
    }
}
~~~

**基于模板实现**

由于大部分的容器其实操作基本不外乎四步：

1. 从待处理的对象中获取 key / namespace ；
2. 根据 key / namespace 集合查询获取数据源；
3. 查询出的数据源按 key  / namespace 分组 ；
4. 将分组的数据源处理后填充到对应的待处理对象中；

因此 crane 为 1/2/4 步提取了两类抽象模板，用户可以基于下述模板快速扩展：

- `top.xiajibagao.crane.core.container.BaseKeyContainer`：根据 key 获取数据源的容器；
- `top.xiajibagao.crane.core.container.BaseNamingContainer`：根据 key 和 namespace 的容器；

比如，若上述自定义容器继承了BaseKeyContainer模板，则代码可以简化为：

~~~Java
@Component
public class UserContainer extends BaseKeyContainer<Integer> implements Container {
    
    @Autowrite
    private UserService userService;
    
    @Override
    protected Map<Integer, User> getSources(@Nonnull Set<Integer> keys) {
        return userSevice.listByIds(userIds).stream()
            .collect(Collectors.toMap(User::getId, Function.identity()));
    }
}
~~~





##  四、字段配置

在此之前，我们需要明确一些概念：

- key 字段：`@Assemble`所注解的字段，该字段的值即我们通常所说的 key 值，或者外键；
- 待处理对象：需要填充的对象；
- 数据源对象：从容器中获取原始数据源；
- 数据源字段： 在`@Prop#src`指定的字段，需要从数据源对象中获取的具体数据对应的 key；
- 引用字段： 在`@Prop#ref`指定的字段，一般对应对应待处理对象中的具体字段；
- 数据源数据：最终需要填充至引用字段。

假如我们已经有一个 `UserContainer`容器，允许我们根据 id 获取对应的 `User` 对象作为数据源，下述示例都基于此实现。

### 1、字段映射

当我们使用 `@Assemble`注解时，可以根据 `@Assemble#props`属性，在入参的`@Prop`注解中自由指定数据源与待处理对象字段间的映射规则，操作者中的操作处理器链将根据映射规则与数据源和待处理对象的数据类型以不同的方式进行处理。

**映射对象**

如果数据源是对象，则我们可以将数据源对象的字段映射到待处理对象中

~~~java
// 根据id查询User对象，然后将其中的name与age字段值映射到待处理对象的userName与userAge字段上
@Assemble(container = UserContainer.class, props = {
    @prop(src = "name", ref = "userName"), 
    @prop(src = "age", ref = "userAge")
})
private String id;
private String userName;
private Integer userAge;
~~~

或者，也可以直接将数据源对象整个映射到待处理对象的字段上：

~~~java
// 根据id查询User对象，然后将其中的name与age字段值映射到待处理对象的userName与userAge字段上
@Assemble(container = UserContainer.class, props = {
    @prop(src = "name", ref = "userName"),
    @prop(src = "age", ref = "userAge"),
    @prop("user") // 将user对象直接映射到待处理对象的user字段上
})
private Integer id;
private String userName;
private Integer userAge;
private User user;
~~~

**映射集合字段**

数据源也可以是集合，我们可以选择直接将集合整个映射到待处理对象的字段上：

~~~java
@Assemble(container = UserContainer.class, props = @prop("users"))
private Integer id;
private List<User> users;
~~~

也可选择只映射集合中对象的某些指定字段：

~~~java
@Assemble(container = UserContainer.class, props = @prop(src = "name", ref = "userNames"))
private Integer id;
private List<String> userNames;
~~~



### 2、字段模板

有时候，尤其对象的字段大多都来自于关联查询时，我们需要在 key 字段上配置的注解就会变得及其臃肿，尤其是当有多个对象需要使用相同的配置时，这个情况会变得更加验证，因此 crane 允许通过 `top.xiajibagao.annotation.@PropsTemplate`将字段配置单独的分离到某个特定的类，然后再在 `@Assemble#propTemplates`属性中指定。

比如，我们现在有这样一个注解：

~~~java
@Assemble(container = UserContainer.class, props = {
    @prop(src = "name", ref = "userName"),
    @prop(src = "age", ref = "userAge"),
    @prop("user") // 将user对象直接映射到待处理对象的user字段上
})
private Integer id;
~~~

我们可以使用一个单独的配置接口，去承担一部分繁琐的字段配置：

~~~java
@PropsTemplate({
    @prop(src = "name", ref = "userName"),
    @prop(src = "age", ref = "userAge")
})
public interface UserPropTemplates {};
~~~

接着我们将原本的注解改为：

~~~java
@Assemble(container = UserContainer.class, props = @prop("user"), propTemplates = {UserPropTemplates.class})
private Integer id;
~~~

即可实现跟原本一样的效果。



### 3、嵌套字段

有时候待处理对象中会存在嵌套的字段，即字段本身是一个集合或者一个对象，内部还需要进行填充处理的情况，这种情况需要使用 `top.xiajibagao.annotation.Disassemble`注解待处理的字段。

比如，待处理对象中存在嵌套的 User 对象集合，我们需要在填待处理对象时一并处理： 

~~~java
// 标记集合中存在类型为User的待处理对象
@Disassemble(User.class)
private List<User> users;
~~~

这样在处理外层对象前，会优先将 `users`字段取出平铺，然后根据注解上的类型对应的操作配置进行填充处理，若嵌套字段中仍然存在 `@Disassemble`，则将继续递归处理，因此，需要注意**不要让两个实例互相循环引用，否则将进入死循环**。

嵌套字段允许是多重嵌套的 Collection 或数组，即可以这样：

~~~java
private List<List<User[]>> users;
~~~

> **注意：不支持除了 Collection 或数组外的其他类型结果。**



### 4、多类型处理

crane 对各种类型数据的操作全部依赖于操作处理器 `top.xiajibagao.crane.core.handler.interfaces.OperateHandler`，一个处理器一般用于处理一种特定类型的数据，类似 Spring 中的 `MessageConverter`。

多个处理器通过 `top.xiajibagao.crane.core.handler.interfaces.OperateHandlerChain`组合为处理器链。当调用时，处理器链将先按指定的顺序排序，然后找到支持处理该类型数据的处理器中优先级最高的完成操作。

若有需要自定义处理的数据类型，可以直接实现 `OperateHandler`接口，并注册到 Spring 容器中的启用的处理器链即可。

以下是不同类型的数据源对象与不同字段配置对应的大致操作结果：

|                 | src为空，数据源：               | src不为空，数据源：                                          | ref为空，待处理对象：                                        | ref不为空，待处理对象：                                      |
| --------------- | ------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| Collection 集合 | 直接将集合作为数据源数据;       | 将集合展开后，从获取的对象获取对应字段作为数据源继续交由处理器链处理； | 将集合展开后，将数据源数据源与展开后的每个待处理对象继续交由数据源对象处理； | 将集合展开后，将数据源数据源与展开后的每个待处理对象继续交由处理器链处理； |
| 数组            | 将数组转为集合并作为数据源数据; | 将数组展开后，从获取的对象获取对应字段作为数据源继续交由处理器链处理； | 将集合展开后，将数据源数据源与展开后的每个待处理对象继续交由数据源对象处理； | 将集合展开后，将数据源数据源与展开后的每个待处理对象继续交由处理器链处理； |
| Map 集合        | 将Map并作为数据源数据;          | 从Map集合中获取 key 对应的值作为数据源数据                   | 将数据源数据填充至Map集合中key字段对应的key中；              | 将数据源数据填充至Map集合对应的key中；                       |
| 对象            | 将对象作为数据源数据；          | 从对象中获取对应字段的值作为数据源数据                       | 将数据源数据填充至待处理对象对应的key字段                    | 将数据源数据填充至待处理对象中的对应字段中；                 |
| 枚举            | 将枚举转为Map并作为数据源数据;  | 从枚举转为的Map集合中获取 key 对应的值作为数据源数据         | 不支持                                                       | 不支持                                                       |
| 基本数据类型    | 将值作为数据源数据；            | 将值作为数据源数据；                                         | 不支持；                                                     | 不支持                                                       |



### 5、数据源预处理

crane 允许通过 SpEl 表达式针对从容器中获取的原始的数据源进行预处理。

比如我们上文举的例子：

~~~java
@Assemble(
    container = IntrospectContainer.class, props = @Prop(
        ref = "userName", 
        exp = "sex == 1 ? #source.name + '先生' ： #source.name + '女士'", // 根据性别，在name后追加“先生”或者“女士”
        expType = String.class // 表达式返回值为String类型
    )
)
private String sex;
private String name;
~~~

根据 `sex`字段从容器中获取的数据源，将先经过表达式的处理，然后将返回指定类型的结果，这个结果将作为新的数据源参与后续处理。

表达式上下文中默认注册了以下变量，允许直接在表达式中引用：

- `#source`：原始数据源对象；
- `#target`：待处理对象；
- `#key`：key字段的值；
- `#src`：`@Prop#src`指定的参数值；
- `#ref`：`@Prop#ref`指定的参数值；

**自定义上下文**

该功能实际由处理器链的表达式包装类 `top.xiajibagao.crane.core.handler.ExpressibleOperateHandlerChain`实现。该类将把一个普通的处理器链包装为支持 SpEL 表达式的处理器链，当数据源进入处理器链前，将由该类先根据表达式进行预处理，然后再将处理后的数据交由处理链处理。

`ExpressibleOperateHandlerChain` 的构造函数允许传入一个类型为 `Supplier<StandardEvaluationContext>`的生产者函数，用户可以自由的向其中注册 Spring 容器、方法或其余的变量。

比如，我们希望注册一个默认变量以及一个默认方法，则可以在配置中如此配置：

~~~java
@ConditionalOnMissingBean(OrderlyOperateHandlerChain.class)
@Bean
public OperateHandlerChain customOperateHandlerChain(OrderlyOperateHandlerChain chain) {
    return new ExpressibleOperateHandlerChain(chain, () -> {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("defName", "游客"); // 注册一个为name的默认变量
        context.registerFunction("isNull", ClassUtil.getDeclaredMethod(Objects.class, "isNull", Object.class)); // 注册一个isNull方法
        return context;
    });
}
~~~

然后可以在注解中这么写：

~~~java
@Assemble(
    container = IntrospectContainer.class, props = @Prop(
        ref = "name", 
        exp = "#isNull(#source) ? #defName : #source", // 若存在用户，则获取用户名，否则获取默认用户名
        expType = String.class
    )
)
private String userId;
private String userName;
~~~

如果希望支持 Ognl 表达式，或者其他需求，则自行扩展该类或依照此创建新的包装类即可。



## 五、工具类

### 1、OperateHelper

`top.xiajibagao.crane.extension.helper.OperateHelper`的作用类似于 Spring 提供提供的各种 XXXTemplate，是对 crane 功能的封装，用于在代码中快速的调用 crane 的填充功能。

`OperateHelper`创建时会默认配置好使用功能所需的各项组件，然后使用时自行选择带有不同方法参数的重载方法即可。

~~~java
// 处理数据，使用自定义的配置类与执行器
operateHelper.process(date, configuration, operationExecutor);
// 处理数据，使用自定义的配置类与默认执行器
operateHelper.process(date, configuration);
// 处理数据，使用默认的解析器解析配置，然后再使用默认执行器执行
operateHelper.process(date);
// 处理数据，使用默认的解析器解析配置，然后再使用自定义的执行器执行
operateHelper.process(date, operationExecutor);
~~~



### 2、方法返回值处理切面

crane 基于 SpringAOP 和 aspectj 实现了方法返回值处理切面`top.xiajibagao.crane.extension.aop.MethodResultProcessAspect`，默认拦截被 `top.xiajibagao.crane.extension.aop.ProcessResult`注解的方法返回值进行填充。

比如：

~~~java
// 自动填充返回的 Classroom 对象
@ProcessResult(Classroom.class)
public Classroom getClassroom(Boolean isHandler) {
    return new Classroom();
}
~~~

其中，可以在`@ProcessResult`自行指定配置解析器，操作者工厂与执行器，切面将在处理时动态从 spring 容器中获取这些组件：

~~~java
@ProcessResult(
    targetClass = Classroom.class,
    operatorFactory = BeanReflexOperatorFactory.class,
    parser = BeanOperateConfigurationParser.class
)
public List<Classroom> getClassroom(Boolean isHandler) {
    return Collections.emptyList();
}
~~~

另外，还可以通过 SpEL 表达式针对方法参数与返回值决定是否需要处理返回值：

~~~java
@ProcessResult(
    targetClass = Classroom.class, 
    condition = "!#result.isEmpty && !#isHandle" // 当返回值为空集合，且isHandle参数不为true时才处理返回值
) 
public List<Classroom> getClassroom(Boolean isHandle) {
    return Collections.emptyList();
}
~~~

>  **注意：切面仅能处理返回值为单个对象、一维度数组或不嵌套的 Collection 集合**。



### 3、动态JSON单元

当数据完全不参与业务操作，并且只需要给前段展示时，我们也可以直接在序列化为 JSON 对象时才填充数据。

由于 Spring 默认的使用的 JSON 库为 jackson，因此 crane 基于 jackson 提供了动态填充处理 JsonNode 对象的 `top.xiajibagao.crane.jackson.impl.module.DynamicJsonNodeModule`。

将其注册到序列化使用的 `ObjectMapper` 中，然后使用 `top.xiajibagao.annotation.ProcessJacksonNode` 注解需要处理的类，则在通过 ObjectMapper 序列化时，将会根据操作配置处理 JsonNode，从而使序列化后获得的 json 串带上对应的数据。

比如我们有如下待序列化的对象：

~~~java
@ProcessJacksonNode
public class Foo {
    private String id;
}
~~~

由于 JsonNode 的特殊性，相比普通的 JavaBean，它可以直接添加或替换对象的属性值。

比如根据 id 动态添加 name 和 age 字段：

~~~java
@ProcessJacksonNode
public class Foo {
    @Assemble(container = UserContainer.class, props = {
        @prop(src = "name", ref = "userName"), 
        @prop(src = "age", ref = "userAge")
    })
    private String id;
}
~~~

我们可以在序列化后得到如下 json 串：

~~~json
{
    "id": 1,
    "userName": "foo",
    "userAge": 12
}
~~~

或者直接替换字段的值：

~~~java
@ProcessJacksonNode
public class Foo {
    @Assemble(container = UserContainer.class, namespace = "sexs")
    private Integer sex;
}
~~~

序列化后得到：

~~~java
{
    "sex": "男"
}
~~~

值得一提的是，`@ProcessJacksonNode`注解与上文提到的 `@ProcessResult`注解一样，也支持指定自定义的配置解析器，操作者工厂和执行器。

## 六、操作配置

### 1、配置类

每一个对象都需要有一个操作配置类用于指导 crane 对其进行填充，操作配置类在 crane 对应 `top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration`下的实现类，并提供了默认实现 `top.xiajibagao.crane.core.parser.BeanOperationConfiguration`。

一个可用的配置类`OperationConfiguration`由三部分组成：

- 字段装配配置：对应`top.xiajibagao.crane.core.parser.interfaces.AssembleOperation`的实现类，代表基于 `@Assemble`注解字段的一次填充操作；
- 字段装卸配置：对应 `top.xiajibagao.crane.core.parser.interfaces.DisassembleOperation`的实现类，代表基于 `@Disassemble`注解的嵌套字段的平摊操作；
- 全局配置：对应  `top.xiajibagao.crane.core.parser.interfaces.GlobalConfiguration`的实现了，代表通过配置文件指定的一些配置；

操作配置类一般可以通过两种方式获取：

- 手动构建；
- 通过注解构和解析器构建；

### 2、基于注解配置

**基本情况**

基于注解即直接在类字段中使用`@Assemble`和 `@Disassemble`注解指定字段操作，然后通过类操作配置解析器解析注解生成操作配置类。

注解式配置的核心在于配置解析器，即 `top.xiajibagao.crane.core.parser.interfaces.OperateConfigurationParser` 接口的实现类。crane 默认提供了基本实现 `top.xiajibagao.crane.core.parser.BeanOperateConfigurationParser`，该解析器将解析注解，并生成 `BeanOperationConfiguration`。

该类大部分关键方法都使用 `protected` 修饰，因此若有自定义的需求——比如需要添加新的注解——可以直接基于 `BeanOperateConfigurationParser` 重写。

crane 的大部分组件默认支持该种方式。

**循环引用**

实际场景中可能存在 A 类中存在类型为 B 的字段，而 B 类中又存在类型为 A 的字段，即类型循环引用的情况。

比如：

~~~java
public class A {
    @Disassemble(B.class)
    private B nestB;
} 

public class B {
    @Disassemble(A.class)
    private A nestA;
} 
~~~

此处 crane 借鉴 spring 的三级缓存，通过一级缓存缓存未构建完成的配置引用从而使解决循环引用问题，因此是允许如此操作的。

### 3、手动构建配置

**基本情况**

实际场景中，可能存在一个类需要同时存在两套配置，或者不太方便直接添加注解的情况，而手动通过构造函数的方式去创建如此复杂的配置实例又不太现实，因此 crane 提供了配置构建辅助类 `top.xiajibagao.crane.core.parser.OperateConfigurationAssistant`用于手动构建操作配置。

比如，我们现在对类 `Person.class`的注解配置如下：

~~~java
public class Person {

    @Assemble(container = TestContainer.class, props = {
        @Prop(src = "beanName", ref = "name"),
        @Prop(src = "beanAge", ref = "age"),
    })
    private Integer id;
    private String name;
    private Integer age;

    @Assemble(container = KeyValueContainer.class, namespace = "sex", props = @Prop("sexName"))
    private Integer sex;
    private String sexName;

    @Assemble(container = EnumDictContainer.class, namespace = "gender", props = {
        @Prop(src = "id", ref = "genderId"),
        @Prop(src = "name", ref = "genderName")
    })
    private Gender gender;
    private Integer genderId;
    private String genderName;

    @Disassemble(Person.class)
    List<Person> relatives;

}
~~~

若使用`OperateConfigurationAssistant`构建则对应如下：

~~~java
OperateConfigurationAssistant<Person> assistant = OperateConfigurationAssistant.basedOnBeanOperationConfiguration(globalConfiguration, Person.class, operatorFactory);
assistant
    .buildAssembler(Person::getGender, enumDictContainer) // 构建装配操作，key 字段为 gender
        .namespace("gender")
        .property("id", Person::getGenderId)
        .property("name", Person::getGenderName)
        .build()
    .buildAssembler(Person::getSex, keyValueContainer)  // 构建装配操作，key 字段为 sex
        .namespace("sex")
        .onlyRefProperty(Person::getSexName)
        .build()
    .buildAssembler(Person::getId, testContainer)  // 构建装配操作，key 字段为 id
        .property("beanName", "name")
        .property("beanAge", "age")
        .build();
	.buildDisassembler(Person::getRelatives, assistant.getConfiguration())  // 构建装卸操作，平摊字段为 relatives
    	.build();
~~~

此处借鉴了 mybatis-plus 的函数式条件构造器，允许 get/set 方法的 lambda 表达式引用实际字段，避免字段的硬编码。

不过这种方式相比注解式依然更肉眼可见的麻烦，所以个人还是推荐更便利且可读性更高的注解式配置。

**循环引用**

当手动配置存在循环引用时，需要自行处理，如上述例子的 `relatives`字段。

### 4、配置缓存

配置解析涉及到大量对象的创建与反射调用，并且在解析的类中存在较多嵌套对象时还需要进行多次递归，因此一般推荐配置类作为单例使用，并且提供了默认的配置缓存 `top.xiajibagao.crane.extension.cache.ConfigurationCache`。

该接口提供了一个基于本地缓存的默认实现 `top.xiajibagao.crane.extension.cache.OperationConfigurationCache`。

此外，针对使用较多的配置解析器，默认提供了一个`ConfigurationCache`实现的带缓存功能的配置解析器包装类 `top.xiajibagao.crane.extension.cache.CacheConfigurationParserWrapper`，该包装类允许包装一个普通的配置解析器，并使其在解析后能够自动缓存解析配置。

## 七、操作配置执行器

操作执行器在 crane 中对应 `top.xiajibagao.crane.core.executor.OperationExecutor`接口的实现类，他是基于操作配置，容器与操作类的更高一层抽象，用于根据操作配置驱动完成待处理对象的每一个字段。

默认提供了三种实现：

- 同步的无序执行器`top.xiajibagao.crane.core.executor.AsyncUnorderedOperationExecutor`：无视操作配置指定的执行顺序，按容器优先的排序然后依次完成不同容器中的操作；
- 异步的无序执行器`top.xiajibagao.crane.core.executor.UnorderedOperationExecutor`：无视操作配置指定的执行顺序，然后以容器为单位异步的完成不同容器中的操作，仅保证按容器优先级提交任务，不保证实际的执行顺序。
- 同步的顺序执行器`top.xiajibagao.crane.core.executor.SequentialOperationExecutor`：严格按照操作配置指定的执行顺序完成不同容器中的操作；

>  **注意：`SequentialOperationExecutor`的排序算法并不高效，因此除非必要，最好自定义（如果能顺便给我提个pr就更好了）或尽可能少用。**



## 待开发功能

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
  
- [ ] 发布 v1.0.0 版本；

