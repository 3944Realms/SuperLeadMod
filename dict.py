#   Super Lead rope mod
#   Copyright (C)  2025  R3944Realms
#   This program is free software: you can redistribute it and/or modify
#   it under the terms of the GNU General Public License as published by
#   the Free Software Foundation, either version 3 of the License, or
#   (at your option) any later version.
#   This program is distributed in the hope that it will be useful,
#   but WITHOUT ANY WARRANTY; without even the implied warranty of
#   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#   GNU General Public License for more details.
#   You should have received a copy of the GNU General Public License
#   along with this program.  If not, see <https://www.gnu.org/licenses/>.
import os
import random

# 确保输出目录存在
os.makedirs("dict", exist_ok=True)

# 全局已使用名字集合，保证三类字典互不重复
used_names = set()

def gen_dict(count: int, length: int, filename: str, charset: str):
    """
    生成字典文件：
    - 所有字符都来自charset（纯中文汉字，包括易经符号）
    - 保证全局唯一
    """
    if length < 1:
        raise ValueError("长度必须至少为 1")

    chars = list(charset)

    # 计算最大可能组合数
    max_combinations = len(chars) ** length
    if count > max_combinations:
        raise ValueError(f"要求数量 {count} 超过最大可能组合数 {max_combinations}")

    generated = set()
    while len(generated) < count:
        # 随机生成纯中文字符串
        s = ''.join(random.choice(chars) for _ in range(length))

        if s in used_names:
            continue  # 跳过已使用的名字
        generated.add(s)
        used_names.add(s)

    with open(filename, "w", encoding='utf-8') as f:
        for s in sorted(generated):  # 排序便于查看
            f.write(s + "\n")

    print(f"生成完成，共 {len(generated)} 个写入 {filename}")

def get_chinese_chars():
    """
    获取中文汉字字符集，包括：
    1. 易经符号
    2. 常用汉字
    3. 扩展汉字
    """
    # 1. 易经符号字符集 (64卦)
    yijing_chars = "䷀䷁䷂䷃䷄䷅䷆䷇䷈䷉䷊䷋䷌䷍䷎䷏䷐䷑䷒䷓䷔䷕䷖䷗䷘䷙䷚䷛䷜䷝䷞䷟䷠䷡䷢䷣䷤䷥䷦䷧䷨䷩䷪䷫䷬䷭䷮䷯䷰䷱䷲䷳䷴䷵䷶䷷䷸䷹䷺䷻䷼䷽䷾䷿"

    # 2. 常用汉字 (约3500个常用字)
    common_chinese = (
        "的一是不了在人有我他个大中来上们到说国和地也子时道出而要于就下得可你年生自会"
        "那后能对着事其里所去行过家十用发天如然作方成者多日都三小军二无同么经法当起与"
        "好看进学中第样道还法理文心现所政美手知明机高长部见定体此心合表化加动系表实新"
        "量将两从问力等电开五心只实社资事制政济用所向好战无性前反合斗图把结第里正新开"
        "论之物从当两些还天资事队批如应形想制心样干都向变关点育重其思与间内去因件日利"
        "相由压员气业代全组数果期导平各基或月毛然问比展那它最及外没看治提五解系林者米"
        "群头意只明四道马认次文通但条较克又公孔领军流入接席位情运器并飞原油放立题质指"
        "建区验活众很教决特此常石强极土少已根共直团统式转别造切九你取西持总料连任志观"
        "调七山程百报更见必真保热委手改管处己将修支识病象先老光专几什六型具示复安带每"
        "东增则完风回南广劳轮科北打积车计给节做务被整联步类集号列温装即毫轴知研单色坚"
        "据速防史拉世设达尔场织历花受求传口断况采精金界品判参层止边清至万确究书低术状"
        "厂须离再目海交权且儿青才证越际八试规斯近注办布门铁需走议县兵虫固除般引齿千胜"
        "细影济白格效置推空配刀叶率今选养德话查差半敌始片施响收华觉备名红续均药标记难"
        "存测士身紧液派准斤角降维板许破述技消底床田势端感往神便圆村构照容非搞亚磨族火"
        "段算适讲按值美态黄易彪服早班麦削信排台声该击素张密害侯草何树肥继右属市严径螺"
        "检左页抗苏显苦英快称坏移约巴材省黑武培著河帝仅针怎植京助升王眼她抓含苗副杂普"
        "谈围食射源例致酸旧却充足短划剂宣环落首尺波承粉践府鱼随考刻靠够满夫失包住促枝"
        "局菌杆周护岩师举曲春元超负砂封换太模贫减阳扬江析亩木言球朝医校古呢稻宋听唯输"
        "滑站另卫字鼓刚写刘微略范供阿块某功套友限项余倒卷创律雨让骨远帮初皮播优占死毒"
        "圈伟季训控激找叫云互跟裂粮粒母练塞钢顶策双留误础吸阻故寸晚丝女焊攻株亲院冷彻"
        "弹错散尼盾商视艺灭版烈零室轻血倍缺厘泵察绝富城冲喷壤简否柱李望盘磁雄似困巩益"
        "洲脱投送奴侧润盖挥距触星松获独官混纪座依未突架宽冬兴章湿偏纹执矿寨责阀熟吃稳"
        "夺硬价努翻奇甲预职评读背协损棉侵灰虽矛厚罗泥辟告卵箱掌氧恩爱停曾溶营终纲孟钱"
        "待尽俄缩沙退陈讨奋械胞幼哪剥迫旋征槽倒握担仍呀鲜吧卡粗介钻逐弱脚怕盐末阴丰编"
        "印蜂急扩伤飞域露核缘游振操央伍域甚迅辉异序免纸夜乡久隶缸夹念兰映沟乙吗儒杀汽"
        "磷艰晶插埃燃欢铁补咱芽永瓦倾阵碳演威附牙芽永瓦斜灌欧献顺猪洋腐请透司危括脉宜"
        "笑若尾束壮暴企菜穗楚汉愈绿拖牛份染既秋遍锻玉夏疗尖殖井费州访吹荣铜沿替滚客召"
        "旱悟刺脑措贯藏令隙"
    )

    # 3. 扩展汉字 (Unicode CJK统一表意文字部分)
    extended_chinese = []
    # 添加一些Unicode范围的汉字（为了避免文件过大，我们只取一部分）
    ranges = [
        (0x4E00, 0x4EFF),  # 基本汉字
        (0x4F00, 0x4FFF),  # 基本汉字
        (0x3400, 0x34FF),  # 扩展A区
    ]

    for start, end in ranges:
        for code in range(start, end + 1):
            try:
                char = chr(code)
                # 过滤掉一些非常用的字符
                if random.random() < 0.3:  # 只取30%的字符，避免过多
                    extended_chinese.append(char)
            except:
                pass

    # 合并所有中文字符
    all_chinese =  common_chinese + ''.join(extended_chinese)

    # 去重并返回
    return ''.join(sorted(set(all_chinese)))

if __name__ == "__main__":
    # 获取纯中文汉字字符集
    chinese_charset = get_chinese_chars()
    print(f"字符集大小：{len(chinese_charset)} 个汉字（包含易经符号）")
    print(f"前50个字符示例：{chinese_charset[:50]}")

    # 生成类名字典（纯中文）
    gen_dict(
        count=3000,
        length=128,  # 类名长度稍短
        filename="dict/class_dict.txt",
        charset=chinese_charset
    )

    # 生成字段/方法字典（纯中文）
    gen_dict(
        count=5000,
        length=128,  # 字段/方法名更短
        filename="dict/member_dict.txt",
        charset=chinese_charset
    )

    # 生成包名字典（纯中文）
    gen_dict(
        count=2000,
        length=128,  # 包名可以更长
        filename="dict/package_dict.txt",
        charset=chinese_charset
    )