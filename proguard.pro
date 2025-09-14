########################################
# Super Lead Rope ProGuard 配置
# 作者: R3944Realms
# 适用于 Minecraft 1.20.1 + Forge
########################################

#---------------------------------------
# 保留主类 (Forge @Mod 入口)
#---------------------------------------
-keep public class top.r3944realms.superleadrope.SuperLeadRope {
    public <init>();
}

# 保留 Forge EventBus 订阅方法
-keepclassmembers class ** {
    @net.minecraftforge.eventbus.api.SubscribeEvent <methods>;
}

#---------------------------------------
# 保留注解、调试信息
#---------------------------------------
-keepattributes *Annotation*,Exceptions,InnerClasses,Signature,SourceFile,LineNumberTable,StackMapTable

#---------------------------------------
# 保留 Minecraft / Forge / Mixin
#---------------------------------------
-keep class net.minecraft.**
-keepclassmembers class net.minecraft.** { *; }
-dontwarn net.minecraft.**

-keep class net.minecraftforge.**
-keepclassmembers class net.minecraftforge.** { *; }
-dontwarn net.minecraftforge.**

-keep class cpw.mods.**
-keepclassmembers class cpw.mods.** { *; }
-dontwarn cpw.mods.**

-keep class mezz.jei.**
-keepclassmembers class mezz.jei.**{ *; }
-dontwarn mezz.jei.**

#---------------------------------------
# 保留资源文件 (mods.toml / assets / data / pack.mcmeta)
#---------------------------------------
-keepdirectories META-INF,assets/**,data/**

#---------------------------------------
# 混淆配置
#---------------------------------------
-renamesourcefileattribute SourceFile

# 可选字典 (需手动提供 dict.txt)
-classobfuscationdictionary dict/class_dict.txt
-obfuscationdictionary dict/member_dict.txt
-packageobfuscationdictionary dict/package_dict.txt

# 全局 repackage (避免 Forge 找不到入口)
-repackageclasses 'contents'

#---------------------------------------
# 构造器保留
#---------------------------------------
-keepclassmembernames class * {
    <init>(...);
    void <clinit>();
}

# 允许混淆内部类
-keep,allowobfuscation class *$* { *; }

#---------------------------------------
# 稳定性配置 (避免奇怪验证错误)
#---------------------------------------
-dontshrink
-dontoptimize
# -dontpreverify

#---------------------------------------
# 输出映射文件
#---------------------------------------
-printmapping build/libs/0xn-mapping.txt
