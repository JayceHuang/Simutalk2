/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ztspeech.weibo.sdk.kaixin;

import android.os.Bundle;


/**
 * ��֤����Ȩ����������
 */
public interface KaixinAuthListener {
	/**
	 * ��Ȩ���
	 * 
	 * @param values
	 *            ��Ȩ���������ص�key-value��ʽ�Ĳ�������keyΪ��������valueΪ����ֵ
	 */
	public void onAuthComplete(Bundle values);

	/**
	 * ��Ȩ���������ش���
	 * 
	 * @param kaixinAuthError
	 *            ��װ���������صĴ�����Ϣ
	 */
	public void onAuthError(KaixinAuthError kaixinAuthError);

	/**
	 * �û�ȡ����¼
	 */
	public void onAuthCancelLogin();

	/**
	 * �û�ȡ����Ȩ
	 * 
	 * @param values
	 *            ��Ȩ���������ص�key-value��ʽ�Ĳ�������keyΪ��������valueΪ����ֵ
	 */
	public void onAuthCancel(Bundle values);
}
