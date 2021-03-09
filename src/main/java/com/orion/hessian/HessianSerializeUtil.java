package com.orion.hessian;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * HessianSerializeUtil
 *
 * @author li.lc
 */
public class HessianSerializeUtil {
    public static <T> byte[] serialize(T obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Hessian2Output hessian2Output = new Hessian2Output(bos);
        try {
            //hessian2Output.startMessage();
            hessian2Output.writeObject(obj);
            hessian2Output.flush();
            //hessian2Output.completeMessage();
            byte[] bytes = bos.toByteArray();
            return bytes;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                hessian2Output.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    public static <T> Object deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        Hessian2Input hessian2Input = new Hessian2Input(bis);
        try {
            //hessian2Input.startMessage();
            Object object = hessian2Input.readObject(clazz);
            //hessian2Input.completeMessage();
            return object;
        }finally {
            hessian2Input.close();
            bis.close();
        }
    }

    public static void main(String[] args) {
        Miracle miracle = new Miracle();
        miracle.setId(11L);
        miracle.setAge(23);
        miracle.setBirthDay(new Date());
        miracle.setName("小红");

        System.out.println(HessianSerializeUtil.serialize(miracle));
    }
}
