package caveControl;

import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.MapGenCaves;
/**
 *
 * @author Zeno410
 */
public class MapGenCaveControl extends MapGenCaves {

    public static Logger logger = new Zeno410Logger("MapGenCaves").logger();
    private final int sizeControl;
    private final int frequencyControl;

    public MapGenCaveControl(int sizeControl, int frequencyControl) {
        this.sizeControl = sizeControl;
        this.frequencyControl = sizeControl;
        logger.info("size "+ sizeControl + "frequency "+ frequencyControl);
    }
    @Override
    protected void func_151538_a(World p_151538_1_, int p_151538_2_, int p_151538_3_, int p_151538_4_, int p_151538_5_, Block[] p_151538_6_) {
        int i1 = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(sizeControl) + 1) + 1);

        //logger.info(" "+ p_151538_2_+" "+ p_151538_3_+" "+p_151538_4_+" "+p_151538_5_ + " size" + sizeControl);
        if (this.rand.nextInt(frequencyControl) != 0)
        {
            i1 = 0;
        }

        for (int j1 = 0; j1 < i1; ++j1)
        {
            double d0 = (double)(p_151538_2_ * 16 + this.rand.nextInt(16));
            double d1 = (double)this.rand.nextInt(this.rand.nextInt(120) + 8);
            double d2 = (double)(p_151538_3_ * 16 + this.rand.nextInt(16));
            int k1 = 1;

            if (this.rand.nextInt(4) == 0)
            {
                this.func_151542_a(this.rand.nextLong(), p_151538_4_, p_151538_5_, p_151538_6_, d0, d1, d2);
                k1 += this.rand.nextInt(4);
            }

            for (int l1 = 0; l1 < k1; ++l1)
            {
                float f = this.rand.nextFloat() * (float)Math.PI * 2.0F;
                float f1 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float f2 = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();

                if (this.rand.nextInt(10) == 0)
                {
                    f2 *= this.rand.nextFloat() * this.rand.nextFloat() * 3.0F + 1.0F;
                }

                this.func_151541_a(this.rand.nextLong(), p_151538_4_, p_151538_5_, p_151538_6_, d0, d1, d2, f2, f, f1, 0, 0, 1.0D);
            }
        }    }

}
