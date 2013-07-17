/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package de.unioninvestment.eai.portal.support.vaadin.groovy

import static org.mockito.Mockito.*

import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.mockito.MockitoAnnotations
import org.vaadin.addon.JFreeChartWrapper
import org.vaadin.peter.contextmenu.ContextMenu
import org.vaadin.svg.SvgComponent
import org.vaadin.svg.SvgComponent.SvgMessageEvent

import com.vaadin.event.MouseEvents.ClickEvent
import com.vaadin.server.ExternalResource
import com.vaadin.server.Sizeable
import com.vaadin.server.StreamResource
import com.vaadin.server.Sizeable.Unit
import com.vaadin.ui.Button
import com.vaadin.ui.CheckBox
import com.vaadin.ui.Embedded
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.Link
import com.vaadin.ui.Select
import com.vaadin.ui.Table
import com.vaadin.ui.Tree
import com.vaadin.ui.Upload
import com.vaadin.ui.VerticalLayout

import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext

class VaadinBuilderTest {

	@Rule
	public LiferayContext liferayContext = new LiferayContext()

	private VaadinBuilder builder;

	@Before
	void setup() {
		MockitoAnnotations.initMocks(this);
		builder = new VaadinBuilder();
	}

	@Test
	void shouldCreateButton() {
		def button = builder.button();
		assert button instanceof Button;
	}

	@Test
	void shouldCreateButtonWithValueAsID() {
		def caption = 'caption'
		Button button = builder.button('myButton', caption:'Test');

		assert builder.components.myButton == button
	}

	@Test
	void shouldCreateButtonWithCaption() {
		Button button = builder.button(caption: 'caption');
		assert button.caption == 'caption';
	}

	@Test
	void shouldCreateButtonWithOnclickHandler() {
		def isListenerCalled;
		Button button = builder.button(onclick: { isListenerCalled = true; });

		button.fireClick()
		assert isListenerCalled;
	}

	@Test
	void shouldCreateCheckBoxWithOnvaluechangeHandler() {
		def isListenerCalled;
		CheckBox checkBox = builder.checkBox(immediate: true, onvaluechange: { isListenerCalled = true; });

		checkBox.value = true
		assert isListenerCalled;
	}

	@Test
	void shouldCreateEmbeddedComponentWithOnclickHandler(){
		def isListenerCalled
		Embedded embedded=builder.embedded(onclick:{ isListenerCalled=true; });
		embedded.fireEvent(new ClickEvent(embedded, null))
		assert isListenerCalled
	}

	@Test
	void shouldCreateSvgComponentWithOnSvgMessageHandler(){
		boolean isListenerCalled
		SvgComponent svgComponent=builder.svgComponent(onSvgMessage:{ isListenerCalled=true; });
		svgComponent.fireEvent(new SvgMessageEvent(svgComponent, "test"))
		assert isListenerCalled
	}

	@Test
	void shouldCreateEmbeddedComponentWithExternalResource(){
		Embedded embedded = builder.embedded( source: builder.externalResource('http://test.local/icon.svg') )
		assert embedded.source instanceof ExternalResource
		assert embedded.source.getURL() == 'http://test.local/icon.svg'
	}

	@Test
	void shouldCreateExternalResource() {
		Button button = builder.button( icon: builder.externalResource('http://test.local/icon.png') )

		assert button.icon instanceof ExternalResource
		assert button.icon.getURL() == 'http://test.local/icon.png'
	}

	@Test
	void shouldCreateStreamResource() {
		Link link = builder.link( resource: builder.streamResource(
		stream: {
			new ByteArrayInputStream("asddas".bytes)
		},
		filename: 'abc.txt',
		mimetype: 'text/plain')
		)

		assert link.resource instanceof StreamResource
		assert link.resource.filename == 'abc.txt'
	}

	@Test
	void shouldCreateLabel() {
		Label label = builder.label(caption: 'text');

		assert label.caption == 'text'
	}

	@Test
	void shouldCreateLabelWithId() {
		builder.verticalLayout {
			label('mylabel', caption: 'text')
		}

		assert builder.components.mylabel.caption == 'text'
	}

	@Test
	void shouldAssignStringWidth() {
		Label label = builder.label(caption: 'text', width:'100%');

		assert label.width == 100f
		assert label.widthUnits == Unit.PERCENTAGE
	}

	@Test
	void shouldAssignStringHeight() {
		Label label = builder.label(caption: 'text', height:'100%');

		assert label.height == 100f
		assert label.heightUnits == Unit.PERCENTAGE
	}

	@Test
	void shouldCreateVerticalLayoutWithComponents() {
		VerticalLayout layout = builder.verticalLayout() { button() }

		assert layout.componentCount == 1
	}

	@Test
	void shouldCreateHorizontalLayoutWithComponents() {
		HorizontalLayout layout = builder.horizontalLayout() { button() }

		assert layout.componentCount == 1
	}

	@Test
	void shouldCreateHorizontalLayoutWithMargin() {
		HorizontalLayout layout = builder.horizontalLayout(margin:true) {}
		assert layout.margin.bitMask == 15
	}

	@Test
	void shouldCreateTable() {
		def columns = [
			"column-1" : { new Label("label-1") },
			"column-2" : { new Label("label-2") },
			"column-3" : { new Label("label-3") }]
		Table table = builder.table(caption:"table-caption", columns:columns)

		assert table
		assert table instanceof Table

		assert table.getColumnGenerator("column-1");
		assert table.getColumnGenerator("column-2");
		assert table.getColumnGenerator("column-3");
	}

	@Test
	void shouldCreateContextMenu() {
		ContextMenu menu = builder.contextMenu(
				items: {
					item('Test')
					item('Test2', data: 4711)
				}
				);
		assert menu != null

		def items = menu.getState().getRootItems()
		assert items[0].caption == 'Test'
		assert items[1].caption == 'Test2'
	}

	@Test @Ignore
	void shouldAddContextMenuToMainWindow() {
		ContextMenu menu = builder.contextMenu();
		// FIXME assert missing
	}

	@Test
	void shouldCreateChart() {
		JFreeChartWrapper chart = builder.chart();
		assert chart != null
	}

	@Test
	void shouldCreatePanelWithContent() {
		def panel = builder.panel {
			verticalLayout { label(caption:'Test') }
		}
		assert panel.content instanceof VerticalLayout
	}

	@Test(expected=AssertionError.class)
	void shouldFailOnPanelWithTwoComponents() {
		def panel = builder.panel {
			label(caption:'Ok')
			label(caption:'Fail')
		}
	}

	@Test
	void shouldCreateChartWithWidth() {
		JFreeChartWrapper chart = builder.chart(width: '100%');
		assert chart.width == 100f
		assert chart.widthUnits == Sizeable.UNITS_PERCENTAGE
	}

	@Test
	void shouldCreateTreeWithOnvaluechangeHandler() {
		def isListenerCalled;
		Tree tree = builder.tree(onvaluechange: { isListenerCalled = true; });

		tree.fireValueChange(true);
		assert isListenerCalled;
	}

	@Test
	void shouldCreateComboBoxWithItems() {
		Select select = builder.select(items: [a:'1',b:'2',c:'3'])
		assert select.getItem('a') != null
		assert select.getItemCaption('a') == '1'
	}
	
	@Test
	void shouldCreateUploadWithListeners() {
		def isUploadSuccessCalled;
		def isUploadFailedCalled;
		Upload upload = builder.upload(onsuccess: {isUploadSuccessCalled = true;}, onfailure: {isUploadFailedCalled = true});
		assert upload != null

		upload.fireUploadSuccess("", "", 0);
		assert isUploadSuccessCalled

		upload.fireUploadInterrupted("", "", 0)
		assert isUploadFailedCalled
	}
}
