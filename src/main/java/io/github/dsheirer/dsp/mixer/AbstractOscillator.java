/*******************************************************************************
 * sdr-trunk
 * Copyright (C) 2014-2018 Dennis Sheirer
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by  the Free Software Foundation, either version 3 of the License, or  (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful,  but WITHOUT ANY WARRANTY; without even the implied
 * warranty of  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License  along with this program.
 * If not, see <http://www.gnu.org/licenses/>
 *
 ******************************************************************************/
package io.github.dsheirer.dsp.mixer;

import io.github.dsheirer.sample.buffer.ReusableBuffer;
import io.github.dsheirer.sample.buffer.ReusableComplexBuffer;
import io.github.dsheirer.sample.complex.Complex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.FloatBuffer;

public abstract class AbstractOscillator implements IOscillator
{
    private final static Logger mLog = LoggerFactory.getLogger(AbstractOscillator.class);

    private FloatBuffer mSampleBuffer;
    private boolean mEnabled;
    private double mFrequency;
    private double mSampleRate;

    public AbstractOscillator(double frequency, double sampleRate)
    {
        mFrequency = frequency;
        mSampleRate = sampleRate;
        mEnabled = frequency != 0;
        update();
    }

    /**
     * Frequency of the tone being generated by this oscillator
     */
    public double getFrequency()
    {
        return mFrequency;
    }

    /**
     * Sets or changes the frequency of this oscillator
     */
    public void setFrequency(double frequency)
    {
        mEnabled = frequency != 0;
        mFrequency = frequency;
        update();
    }

    /**
     * Sample rate of this oscillator
     */
    public double getSampleRate()
    {
        return mSampleRate;
    }

    /**
     * Sets or changes the sample rate of this oscillator
     */
    public void setSampleRate(double sampleRate)
    {
        mSampleRate = sampleRate;
        update();
    }

    /**
     * Indicates if this oscillator has a non-zero frequency setting.  When used as a frequency translating
     * mixer, setting a frequency of zero indicates that no frequency correction is needed, so this flag can
     * indicate when to apply the oscillator to frequency correct samples.
     *
     * @return true if the current frequency value is non-zero.
     */
    @Override
    public boolean isEnabled()
    {
        return mEnabled;
    }

    /**
     * Commands sub-class implementations to update internal structures to account for changes in frequency or
     * sample rate.  The boolean reset indicator optionally commands a reset of the current I/Q values.
     */
    protected abstract void update();

    /**
     * Performs complex heterodyne against the samples using this oscillator
     * @param samples to mix with this oscillator
     */
    @Override
    public float[] mixComplex(float[] samples)
    {
        for(int x = 0; x < samples.length; x += 2)
        {
            float i = Complex.multiplyInphase(samples[x], samples[x + 1], inphase(), quadrature());
            float q = Complex.multiplyQuadrature(samples[x], samples[x + 1], inphase(), quadrature());

            samples[x] = i;
            samples[x + 1] = q;

            rotate();
        }

        return samples;
    }

    /**
     * Generates an array of real samples from this oscillator.
     * @param sampleCount number of samples to generate and length of the resulting float array.
     * @return
     */
    @Override
    public float[] generateReal(int sampleCount)
    {
        float[] samples = new float[sampleCount];

        for(int x = 0; x < sampleCount; x++)
        {
            samples[x] = inphase();
            rotate();
        }

        return samples;
    }

    /**
     * Generates an array of complex samples from this oscillator.
     * @param sampleCount number of samples to generate and length of the resulting float array.
     * @return
     */
    @Override
    public float[] generateComplex(int sampleCount)
    {
        float[] samples = new float[sampleCount * 2];

        for(int x = 0; x < sampleCount * 2; x += 2)
        {
            samples[x] = inphase();
            samples[x + 1] = quadrature();
            rotate();
        }

        return samples;
    }

    /**
     * Generates enough complex samples to fill the reusable complex buffer
     * @param reusableComplexBuffer to fill with complex samples
     */
    @Override
    public void generateComplex(ReusableComplexBuffer reusableComplexBuffer)
    {
        int sampleCount = reusableComplexBuffer.getSampleCount();

        if(mSampleBuffer == null || mSampleBuffer.capacity() != sampleCount * 2)
        {
            mSampleBuffer = FloatBuffer.allocate(sampleCount * 2);
        }

        mSampleBuffer.rewind();

        for(int x = 0; x < sampleCount; x++)
        {
            mSampleBuffer.put(inphase());
            mSampleBuffer.put(quadrature());
            rotate();
        }

        reusableComplexBuffer.reloadFrom(mSampleBuffer, System.currentTimeMillis());
    }

    /**
     * Generates enough real samples to fill the reusable buffer
     * @param reusableBuffer to fill with real samples
     */
    @Override
    public void generateReal(ReusableBuffer reusableBuffer)
    {
        reusableBuffer.reloadFrom(generateReal(reusableBuffer.getSampleCount()), System.currentTimeMillis());
    }
}
