/*
 * Encog(tm) Java Examples v3.3
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-examples
 *
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package stockmarket;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.encog.ConsoleStatusReportable;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.market.MarketDataDescription;
import org.encog.ml.data.market.MarketDataType;
import org.encog.ml.data.market.MarketMLDataSet;
import org.encog.ml.data.market.loader.MarketLoader;
import org.encog.ml.data.market.loader.YahooFinanceLoader;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.pattern.FeedForwardPattern;
import org.encog.neural.prune.PruneIncremental;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.simple.EncogUtility;

public class MarketPrune {

	public static void incremental(File dataDir) {
		File file = new File(dataDir, Config.TRAINING_FILE);

		if (!file.exists()) {
			System.out.println("Can't read file: " + file.getAbsolutePath());
			return;
		}

		MLDataSet training = EncogUtility.loadEGB2Memory(file);

		FeedForwardPattern pattern = new FeedForwardPattern();
		pattern.setInputNeurons(training.getInputSize());
		pattern.setOutputNeurons(training.getIdealSize());
		pattern.setActivationFunction(new ActivationTANH());

		
		File file2 = new File(dataDir,Config.NETWORK_FILE);

		if (!file2.exists()) {
			System.out.println("Can't read file: " + file.getAbsolutePath());
			return;
		}
		MarketMLDataSet evaluate = grabData();
		
		
		
		long iterations = ((long)999999999)*999999900;
		PruneIncremental prune = new PruneIncremental(training, evaluate , pattern, iterations, 4, 10,
				new ConsoleStatusReportable());

		prune.addHiddenLayer(1, 40);
		prune.addHiddenLayer(0, 40);
		//prune.addHiddenLayer(15, 30);

		prune.process();

		File networkFile = new File(dataDir, Config.NETWORK_FILE);
		EncogDirectoryPersistence.saveObject(networkFile, prune.getBestNetwork());

	}
	public static MarketMLDataSet grabData() {
		MarketLoader loader = new YahooFinanceLoader();
		MarketMLDataSet result = new MarketMLDataSet(loader,
				Config.INPUT_WINDOW, Config.PREDICT_WINDOW);
		MarketDataDescription desc = new MarketDataDescription(Config.TICKER,
				MarketDataType.ADJUSTED_CLOSE, true, true);
		result.addDescription(desc);

		Calendar end = new GregorianCalendar();// end today
		Calendar begin = (Calendar) end.clone();// begin 30 days ago
		begin.add(Calendar.DATE, -150);
		

		result.load(begin.getTime(), end.getTime());
		result.generate();

		return result;

	}

	
}
